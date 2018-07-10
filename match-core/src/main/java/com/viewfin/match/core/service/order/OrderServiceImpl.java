package com.viewfin.match.core.service.order;

import com.viewfin.match.core.component.disruptor.OrderProducer;
import com.viewfin.match.core.conf.OrderConfig;
import com.viewfin.match.core.exception.ErrorInfo;
import com.viewfin.match.core.entity.Order;
import com.viewfin.match.core.enums.OrderOpsEnum;
import com.viewfin.match.core.enums.OrderSideEnum;
import com.viewfin.match.core.enums.StatusCodeEnum;
import com.viewfin.match.core.exception.Result;
import com.viewfin.match.core.service.strategy.AbstractMatchStrategy;
import com.viewfin.match.core.util.BeanUtil;
import com.viewfin.match.core.component.spring.SpringContextSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @Auther: pangzhiwang
 * @Date: 2018/7/4 17:37
 * @Description:
 */
@Service
public class OrderServiceImpl implements  OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderProducer orderProducer;

    @Autowired
    private ListOperations<String, Long> listOperations;

    @Autowired
    private HashOperations<String, String, Object> hashOperations;

    @Autowired
    private OrderConfig orderConfig;

    private AbstractMatchStrategy matchStrategy;

    @Override
    public void createOrder(Order order, ErrorInfo errorInfo) {

        Long orderId = order.getOrderId();
        String redisOrderDetailKey = orderConfig.getCreateOrderDetailMarket(orderId);
        String redisOrderListKey=orderConfig.getCreareOrderQueueMarket();
        try{
            Map<String, Object> orderMap = (Map<String, Object>)BeanUtil.describe(order);

            //@TODO lua 保证数据一致性
            hashOperations.putAll(redisOrderDetailKey,orderMap);
            listOperations.leftPush(redisOrderListKey,orderId);
            orderProducer.onData(order);

        }catch (Exception e){
            errorInfo= new ErrorInfo();
            errorInfo.setCode(StatusCodeEnum.internal_server_error.getCode());
            errorInfo.setMsg(e.getMessage());
            LOGGER.info("creatOrder({}) ,error > {}",order.toString(),e.getMessage());
        }

    }
    @Override
    public void cancelOrder(String orderId, ErrorInfo errorInfo) {
        String cancelOrderListKey=orderConfig.getCancelOrderQueueMarket();
        try{
            Long orderIdLong = new Long(orderId);
            listOperations.leftPush(cancelOrderListKey,orderIdLong);
            PriorityBlockingQueue<Long> cancelQueue= getMatchStrategy().getCancelQueue();
            cancelQueue.offer(new Long(orderId));

            //有异议 @TODO
            //getMatchStrategy().cancelOrder(new Long(orderId));

            Order order = null;
            for(Order order1 :getMatchStrategy().getAskQueue()){
                if(order1.getOrderId().equals(orderIdLong)){
                    order=order1;
                    break;
                }
            }
            if(order==null){
                for(Order order1 :getMatchStrategy().getBidQueue()){
                    if(order1.getOrderId().equals(orderIdLong)){
                        order=order1;
                        break;
                    }
                }
            }
            if(order==null){//撮合完成或者已经在撮合中
                return;
            }
            Order monitorOrder=new Order();
            BeanUtils.copyProperties(order,monitorOrder);
            if(OrderOpsEnum.BUY.getCode().equalsIgnoreCase(order.getSide())){
                monitorOrder.setSide(OrderOpsEnum.SELL.getCode());
                monitorOrder.setTimestamp(-9999999L);//标记本订单是无效订单
            }else{
                monitorOrder.setSide(OrderOpsEnum.BUY.getCode());
                monitorOrder.setTimestamp(-9999999L);//标记本订单是无效订单
            }
            getMatchStrategy().match(monitorOrder);

        }catch (Exception e){
            errorInfo= new ErrorInfo();
            errorInfo.setCode(StatusCodeEnum.internal_server_error.getCode());
            errorInfo.setMsg(e.getMessage());
            LOGGER.info("cancelOrder({}) ,error > {}",orderId,e.getMessage());
        }
    }
    @Override
    public Result<Map<String, PriorityBlockingQueue<Order>>> getDeepData(String market, ErrorInfo errorInfo) {
        try{

            Map<String, PriorityBlockingQueue<Order>>  resultMap = new HashMap<>();
            resultMap.put(OrderSideEnum.BID.getCode(),getMatchStrategy().getBidQueue());
            resultMap.put(OrderSideEnum.ASK.getCode(),getMatchStrategy().getAskQueue());

            Result<Map<String, PriorityBlockingQueue<Order>>> result = Result.success(resultMap);

            return result;

        }catch (Exception e){
            errorInfo= new ErrorInfo();
            errorInfo.setCode(StatusCodeEnum.internal_server_error.getCode());
            errorInfo.setMsg(e.getMessage());
            LOGGER.info("getDeepData({}) ,error > {}",market,e.getMessage());
        }
        return null;
    }


    private AbstractMatchStrategy getMatchStrategy(){
        if(matchStrategy==null){
            synchronized (this){
                if(matchStrategy==null){
                    matchStrategy = SpringContextSupport.getBean(orderConfig.getStrategy(),AbstractMatchStrategy.class);
                }
            }
        }
        return matchStrategy;
    }


}
