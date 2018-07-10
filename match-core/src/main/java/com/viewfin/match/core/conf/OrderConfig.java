package com.viewfin.match.core.conf;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @Auther: pangzhiwang
 * @Date: 2018/7/4 20:00
 * @Description:
 */

@Component
public class OrderConfig   implements InitializingBean {

    @Value("${swirly.kafka.topic}")
    private String topic;

    @Value("${swirly.redis.key.trade.idGen}")
    private String redistradeIdGen;

    @Value("${swirly.redis.createOrder.list}")
    private String redisCreateOrderList;

    @Value("${swirly.redis.createOrder.detail}")
    private String redisCreateOrderDetail;

    @Value("${swirly.redis.cancelOrder.list}")
    private String redisCancelOrderList;

    @Value("${swirly.market}")
    private String market;

    @Value("${swirly.strategy}")
    private String strategy;


    @Value("${rifles.redis.system.door}")
    private String door;


    @Value("${swirly.redis.sendMsgError.hash}")
    private String sendMsgErrorHash;



    /**
     *  ---- market data gen
     */
    String topicMarket;
    String doorMarket;

    String cancelOrderQueueMarket;

    String createOrderDetailMarket;
    String creareOrderQueueMarket;

    String redisTradeIdGenMarket;

    String sendMsgErrorHashMakert;

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    public void init(){
        topicMarket = topic+market;
        doorMarket = String.format(door,market);
        cancelOrderQueueMarket = String.format(redisCancelOrderList,market);
        creareOrderQueueMarket = String.format(redisCreateOrderList,market);

        createOrderDetailMarket = String.format(redisCreateOrderDetail,market);

        sendMsgErrorHashMakert = String.format(sendMsgErrorHash,market);

        redisTradeIdGenMarket = redistradeIdGen;
    }

    public String getMarket() {
        return market;
    }

    public String getDoorMarket() {
        return doorMarket;
    }

    public String getStrategy() {
        return strategy;
    }

    public String getTopicMarket() {
        return topicMarket;
    }


    public String getCancelOrderQueueMarket() {
        return cancelOrderQueueMarket;
    }

    public String getCreateOrderDetailMarket(Long orderId){
        return createOrderDetailMarket+":"+orderId;
    }

    public String getCreareOrderQueueMarket() {
        return creareOrderQueueMarket;
    }

    public String getRedisTradeIdGenMarket() {
        return redisTradeIdGenMarket;
    }

    public String getSendMsgErrorHashMakert() {
        return sendMsgErrorHashMakert;
    }
}
