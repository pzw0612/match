package com.viewfin.match.core.job;

import com.viewfin.match.core.component.kafka.BasicKafkaProducer;
import com.viewfin.match.core.conf.OrderConfig;
import com.viewfin.match.core.entity.Trade;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 任务调度
 *
 * @Description: todo
 * @author: pangzhiwang
 * @create: 2018/5/1
 **/

@Component
@EnableScheduling
public class TaskSchedule {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskSchedule.class);

    @Qualifier("redisTemplate")
    @Autowired
    protected RedisTemplate redisTemplate;

    @Autowired
    private OrderConfig orderConfig;

    @Autowired
    protected BasicKafkaProducer basicProducer;

    @Scheduled(cron = "${swirly.cron.cancelOrderCleanTask}")
    public void cancelOrderCleanTask(){
        try{

            ListOperations<String, Long> listOperations= redisTemplate.opsForList();
            //创建订单列表
            String redisCreateOrderListKey=orderConfig.getCreareOrderQueueMarket();
            List<Long> createOrderList=listOperations.range(redisCreateOrderListKey,0,-1);

            // 撤销订单处理
            String redisCancelOrderListKey=orderConfig.getCancelOrderQueueMarket();
            List<Long> cancelOrderList=listOperations.range(redisCancelOrderListKey,0,-1);
            if(cancelOrderList==null || cancelOrderList.size()==0){
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
            }else{
                // 删除已经撮合完成的撤销订单列表
                if(cancelOrderList!=null  &&  cancelOrderList.size()> 0){
                    cancelOrderList.forEach(orderId -> {
                        listOperations.remove(redisCancelOrderListKey,0,orderId);
                    });
                }
            }
        }catch (Exception e){
            LOGGER.error("cancelOrderCleanTask error,msg> {}",e.getMessage());
        }
    }

    @Scheduled(cron = "${swirly.cron.kafkaMsgRetryTask}")
    public void kafkaMsgRetryTask(){
        try{
            //创建订单列表
            String sendMsgErrorHashMakert=orderConfig.getSendMsgErrorHashMakert();
            Set<String> errorSet=redisTemplate.opsForHash().keys(sendMsgErrorHashMakert);
            if(errorSet==null || errorSet.size()==0){
                return;
            }
            HashOperations<String, Long, Trade> hashOperations = redisTemplate.opsForHash();
            errorSet.forEach(matchId->{
                Trade trade = hashOperations.get(sendMsgErrorHashMakert,matchId);
                if(trade!=null){
                    if(sendMsg(trade)){
                        redisTemplate.opsForHash().delete(sendMsgErrorHashMakert,matchId);
                    }
                }
            });
        }catch (Exception e){
            LOGGER.error("kafkaMsgRetryTask error,msg> {}",e.getMessage());
        }
    }


    private boolean sendMsg(Trade trade){
        try{
            String topic = orderConfig.getTopicMarket();
            basicProducer.send(topic,trade);
            return true;
        }catch (Exception e){
            LOGGER.error("kafka sendMsg retry error,msg:{}",e.getMessage());
            return false;
        }
    }
}
