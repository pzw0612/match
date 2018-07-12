package com.viewfin.match.etcusd;

import com.lmax.disruptor.dsl.Disruptor;
import com.viewfin.match.core.component.disruptor.OrderProducer;
import com.viewfin.match.core.component.spring.SpringContextSupport;
import com.viewfin.match.core.conf.OrderConfig;
import com.viewfin.match.core.entity.Order;
import com.viewfin.match.core.service.strategy.LimitMatchStrategy;
import com.viewfin.match.core.util.BeanUtil;
import com.viewfin.match.core.util.MapUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description: todo
 * @author: pangzhiwang
 * @create: 2018/4/1
 **/
@EnableScheduling
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("com.viewfin.match.core")
public class EtcusdMatchApplication implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(EtcusdMatchApplication.class);

    public static Disruptor<Order> disruptor;

    public static void main(String[] args) {
        try{
            SpringApplication application= new SpringApplication(EtcusdMatchApplication.class);
            application.addListeners(new ApplicationListener<ApplicationReadyEvent>(){
                @Override
                public void onApplicationEvent(ApplicationReadyEvent event) {
                    try{
                        ConfigurableApplicationContext context=event.getApplicationContext();
                        disruptor= context.getBean(Disruptor.class);

                        SpringContextSupport.setApplicationContext(context);
                        OrderConfig orderConfig= context.getBean(OrderConfig.class);

                        //disruptor.start();

                        doBoot(context,orderConfig);
                    }catch (Exception e){
                        LOGGER.error("system start error,error:> {}", e.getMessage());
                        System.exit(1);
                    }
                }
            }, new ApplicationListener<ContextClosedEvent>(){
                        @Override
                        public void onApplicationEvent(ContextClosedEvent event) {
                            try {
                                disruptor.shutdown();
                                TimeUnit.SECONDS.sleep(1l);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );

            application.run(args);
        }catch (Exception e){
            LOGGER.error("main error,msg: " + e.getMessage());
            //System.exit(1);
        }
    }

    @Override
    public void run(String... args) throws Exception {

    }

    // 系统启动逻辑
    public static void  doBoot(ConfigurableApplicationContext context,OrderConfig orderConfig){

        //系统启动流程
        OrderProducer orderProducer= context.getBean(OrderProducer.class);
        ListOperations<String, Long> listOperations= context.getBean(ListOperations.class);

        //创建订单列表
        String redisCreateOrderListKey=orderConfig.getCreareOrderQueueMarket();
        List<Long> createOrderList=listOperations.range(redisCreateOrderListKey,0,-1);

        // 撤销订单处理
        String redisCancelOrderListKey=orderConfig.getCancelOrderQueueMarket();
        List<Long> cancelOrderList=listOperations.range(redisCancelOrderListKey,0,-1);
        if(cancelOrderList==null || cancelOrderList.size()==0){
            //启动上次未完成的订单
            if(createOrderList!=null && createOrderList.size()>0){
                rebootOrder(context, orderConfig, orderProducer, createOrderList);
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
                    LimitMatchStrategy limitMatchStrategy = context.getBean(LimitMatchStrategy.class);
                    limitMatchStrategy.getCancelQueue().offer(orderId);
                });
            }
            rebootOrder(context, orderConfig, orderProducer, createOrderList);
        }else{
            // 删除无效撤销
            if(cancelOrderList!=null  &&  cancelOrderList.size()> 0){
                cancelOrderList.forEach(orderId -> {
                    listOperations.remove(redisCancelOrderListKey,0,orderId);
                });
            }
            listOperations.getOperations().delete(redisCancelOrderListKey);
        }
    }

    private static void rebootOrder(ConfigurableApplicationContext context, OrderConfig orderConfig, OrderProducer orderProducer, List<Long> createOrderList) {
        HashOperations<String, String, Object> hashOperations=context.getBean(HashOperations.class);
        Map<String, Object> orderMap;
        Order order= null;
        for (Long orderId: createOrderList){
            orderMap =hashOperations.entries(orderConfig.getCreateOrderDetailMarket(orderId));

            MapUtils.toBean(orderMap,order);

            if(order==null){
                LOGGER.error("rebootOrder error");
                continue;
            }

            orderProducer.onData(order);
        }


    }

}
