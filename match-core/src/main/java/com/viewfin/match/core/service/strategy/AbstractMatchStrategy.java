package com.viewfin.match.core.service.strategy;

import com.viewfin.match.core.component.disruptor.OrderBuyCompartor;
import com.viewfin.match.core.component.disruptor.OrderSellCompartor;
import com.viewfin.match.core.component.kafka.BasicKafkaProducer;
import com.viewfin.match.core.conf.OrderConfig;
import com.viewfin.match.core.entity.Order;
import com.viewfin.match.core.entity.Trade;
import com.viewfin.match.core.enums.OrderOpsEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * @Description: 撮合策略
 * @author: pangzhiwang
 * @create: 2018/4/7
 **/
public abstract class AbstractMatchStrategy implements MatchService.MatchStrategy {
    protected static final Logger LOGGER = LoggerFactory.getLogger(LimitMatchStrategy.class);

    //买单队列
    protected volatile PriorityBlockingQueue<Order> bidQueue =
            new PriorityBlockingQueue<Order>(10000000, new OrderBuyCompartor());
    //卖单队列
    protected volatile PriorityBlockingQueue<Order> askQueue =
            new PriorityBlockingQueue<Order>(10000000, new OrderSellCompartor());
    //取消队列
    protected volatile PriorityBlockingQueue<Long> cancelQueue =
            new PriorityBlockingQueue<>(100000);

    @Autowired
    protected volatile ListOperations<String, Long> listOperations;
    @Autowired
    protected volatile HashOperations<String, String, Object> hashOperations;
    @Autowired
    protected volatile ValueOperations<String, Long> ValueOperations;


    @Autowired
    @Qualifier("redisTemplate")
    protected RedisTemplate redisTemplate;


    @Autowired
    protected BasicKafkaProducer basicProducer;

    @Autowired
    protected OrderConfig orderConfig;

    public PriorityBlockingQueue<Order> getBidQueue() {
        return bidQueue;
    }
    public PriorityBlockingQueue<Order> getAskQueue() {
        return askQueue;
    }
    public PriorityBlockingQueue<Long> getCancelQueue() {
        return cancelQueue;
    }

    /**
     * trade id generator
     *
     * @return
     */
    protected Long genTradeId() {
        Long orderId = ValueOperations.increment(orderConfig.getRedisTradeIdGenMarket(), 1L);
        return orderId;
    }


    //撮合逻辑
    public void match(Order order) {
        try {
            //首先判断订单类型,如果是买单则与卖单进行匹配，匹配成功生成
            if (OrderOpsEnum.BUY.toCode().equals(order.getSide())) {
                doBuy(order);
                return;
            }

            //卖单逻辑
            if (OrderOpsEnum.SELL.toCode().equals(order.getSide())) {
                doSell(order);
                return;
            }
        } catch (Exception e) {
            LOGGER.error("match error,msg > {}", e.getMessage());
        }
    }

//    public boolean cancelOrder(Long orderId) {
//        try {
//            boolean flg= false;
//            for(Order order1 :bidQueue){
//                if (order1.getOrderId().equals(orderId)) {
//                    synchronized (bidQueue) {
//                        flg = cancelOrderHandle(order1,bidQueue);
//                    }
//                    break;
//                }
//            }
//            if (flg){
//                return true;
//            }
//            for(Order order1 :askQueue){
//                if (order1.getOrderId().equals(orderId)) {
//                    synchronized (askQueue) {
//                        flg = cancelOrderHandle(order1,askQueue);
//                    }
//                    break;
//                }
//            }
//            return flg;
//        }catch(Exception e) {
//            LOGGER.error("cancel order error,msg {}",e.getMessage());
//            return false;
//        }
//}
//    private boolean cancelOrderHandle(Order order1,PriorityBlockingQueue<Order> queue) {
//        boolean flg;
//
//            Trade trade = new Trade();
//            trade.setMarket(orderConfig.getMarket());
//            trade.setMatchId(genTradeId());
//            if (OrderOpsEnum.BUY.getCode().equalsIgnoreCase(order1.getSide())) {
//                trade.setBidOrderId(order1.getOrderId());
//                trade.setSide(OrderSideEnum.BID.getCode());
//                trade.setAskOrderId(-1L);
//            }else {
//                trade.setAskOrderId(order1.getOrderId());
//                trade.setSide(OrderSideEnum.ASK.getCode());
//                trade.setBidOrderId(-1L);
//            }
//            trade.setLots(order1.getLots());
//            trade.setTicks(order1.getTicks());
//            trade.setTime(System.currentTimeMillis());
//            trade.setStatus(TradeStatusEnum.cancel.getCode());
//
//            sendMsg(trade);
//
//            listOperations.remove(orderConfig.getCreareOrderQueueMarket(), 0, order1.getOrderId());
//            hashOperations.getOperations().delete(orderConfig.getCreateOrderDetailMarket(order1.getOrderId()));
//
//            queue.remove(order1);
//
//            flg=true;
//
//        return flg;
//    }


    public void doBuy(Order order) {

    }

    public void doSell(Order order) {

    }

    public boolean sendMsg(Trade trade) {
        try {
            String topic = orderConfig.getTopicMarket();
            basicProducer.send(topic, trade);
            return true;
        } catch (Exception e) {
            LOGGER.error("kafka sendMsg error,msg:{}", e.getMessage());
            String sendMsgError = orderConfig.getSendMsgErrorHashMakert();

            hashOperations.put(sendMsgError, trade.getMatchId().toString(), trade);
            return false;
        }
    }

}
