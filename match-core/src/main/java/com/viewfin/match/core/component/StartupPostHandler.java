package com.viewfin.match.core.component;

import com.lmax.disruptor.dsl.Disruptor;
import com.viewfin.match.core.component.disruptor.OrderProducer;
import com.viewfin.match.core.conf.OrderConfig;
import com.viewfin.match.core.entity.Order;
import com.viewfin.match.core.service.strategy.LimitMatchStrategy;
import com.viewfin.match.core.util.MapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 系统重启业务逻辑
 *
 * @Auther: pangzhiwang
 * @Date: 2018/7/12 15:30
 * @Description:
 */

@Component
@Slf4j
public class StartupPostHandler  implements CommandLineRunner {

    @Autowired
    private Disruptor<Order> disruptor;

    @Autowired
    private OrderConfig orderConfig;

    @Autowired
    private OrderProducer orderProducer;

    @Autowired
    private ListOperations<String, Long> listOperations;

    @Autowired
    private  LimitMatchStrategy limitMatchStrategy;

    @Autowired
    private HashOperations<String, String, Object> hashOperations;

    @Override
    public void run(String... args) throws Exception {
        try{
            doBoot();
        }catch (Exception e){
            log.error("doBoot error, msg:> {}" ,e.getMessage());
            e.printStackTrace();
        }

    }


    // 系统启动逻辑
    public  void  doBoot(){
        //创建订单列表
        String redisCreateOrderListKey=orderConfig.getCreareOrderQueueMarket();
        List<Long> createOrderList=listOperations.range(redisCreateOrderListKey,0,-1);

        // 撤销订单处理
        String redisCancelOrderListKey=orderConfig.getCancelOrderQueueMarket();
        List<Long> cancelOrderList=listOperations.range(redisCancelOrderListKey,0,-1);
        if(cancelOrderList==null || cancelOrderList.size()==0){
            //启动上次未完成的订单
            if(createOrderList!=null && createOrderList.size()>0){
                rebootOrder(createOrderList);
            }
            return;
        }
        if(createOrderList!=null && createOrderList.size()>0){
            Collection<Long> sameElements = CollectionUtils.retainAll(cancelOrderList, createOrderList);
            Collection<Long> diffElements = CollectionUtils.subtract(cancelOrderList,sameElements);

            // 删除已经撮合完成的撤销订单列表
            if(diffElements!=null  &&  diffElements.size()> 0){
                diffElements.forEach(orderId -> {
                    listOperations.remove(redisCancelOrderListKey,0,orderId);
                });
            }
            // 保留创建订单和撤销订单 中公共的 撤销列表
            if(sameElements!=null  &&  sameElements.size()> 0){
                sameElements.forEach(orderId -> {
                    limitMatchStrategy.getCancelQueue().offer(orderId);
                });
            }
            rebootOrder(createOrderList);
        }else{
            listOperations.getOperations().delete(redisCancelOrderListKey);
        }
    }

    private  void rebootOrder(List<Long> createOrderList) {
        Map<String, Object> orderMap;

        for (Long orderId: createOrderList){
            orderMap =hashOperations.entries(orderConfig.getCreateOrderDetailMarket(orderId));
            Order order = new Order();
            if(orderMap==null || orderMap.size()==0){
                log.warn(orderConfig.getCreateOrderDetailMarket(orderId) +" have error");
                continue;
            }

            MapUtils.toBean(orderMap,order);

            if(order==null || order.getSide()==null || order.getTicks()==null){
                log.error("rebootOrder error");
                continue;
            }
            orderProducer.onData(order);
        }
    }
}
