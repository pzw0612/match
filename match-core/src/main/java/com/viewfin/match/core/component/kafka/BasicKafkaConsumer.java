package com.viewfin.match.core.component.kafka;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description: todo
 * @author: pangzhiwang
 * @create: 2018/4/1
 **/
//@Component
public class BasicKafkaConsumer {

//    @Value("${kafka.consumer.topic}")
//    private String topic;
//
//    @Autowired
//    @Qualifier("strKeyRedisTemplate")
//    private RedisTemplate redisTemplate;
//
//    @Autowired
//    private OrderProducer orderDisruptorProducer;
//
//    @KafkaListener(topics = "${kafka.consumer.topic}", containerFactory = "kafkaListenerContainerFactory")
//    public void receive(String ordertr) {
//        log.info("order info:"+ordertr);
//        try{
//            Order order = JsonUtils.toJavaBean(ordertr, Order.class);
//            if(OrderOpsEnum.cancle.toCode().equals(order())){
//                //防重复提交
//                if(redisTemplate.opsForHash().hasKey(Constants.swirly_order_cancle_req,String.valueOf(order.getId()))){
//                    return;
//                }else{
//                    redisTemplate.opsForHash().put(Constants.swirly_order_cancle_req,String.valueOf(order.getId()),order);
//                }
//            }else{
//                //防重复提交
//                if(redisTemplate.opsForHash().hasKey(Constants.swirly_order_req,String.valueOf(order.getId()))){
//                    return;
//                }else{
//                    //写入请求队列中
//                    redisTemplate.opsForHash().put(Constants.swirly_order_req,String.valueOf(order.getId()),order);
//                }
//            }
//            orderDisruptorProducer.onData(order);
//        }catch (Exception e){
//            //@TODO 异常处理
//            log.error("消费订单时异常，order:"+ ordertr+",msg:"+ e.getMessage() );
//        }
//    }




}