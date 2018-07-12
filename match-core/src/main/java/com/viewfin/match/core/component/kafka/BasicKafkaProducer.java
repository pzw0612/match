package com.viewfin.match.core.component.kafka;


import com.viewfin.match.core.entity.Trade;
import com.viewfin.match.core.util.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @Description: todo
 * @author: pangzhiwang
 * @create: 2018/4/1
 **/

@EnableKafka
@Component
@Slf4j
public class BasicKafkaProducer {

    //@Qualifier("kafkaTemplate")
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic , Trade trade) {
        Long matchId = trade.getMatchId();
        String id="0";
        if(matchId==null){
            id = String.valueOf(System.nanoTime());
        }else{
            id= String.valueOf(matchId);
        }
        String tradeJson = JSONUtil.toJSONString(trade);

        log.info("trade id {},{}",matchId,tradeJson);
        kafkaTemplate.send(topic,id, tradeJson);
    }
}
