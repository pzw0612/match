package com.viewfin.match.core.conf;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.viewfin.match.core.entity.Order;
import com.viewfin.match.core.component.disruptor.orderEventFactory;
import com.viewfin.match.core.service.strategy.MatchService;
import com.viewfin.match.core.component.spring.SpringContextSupport;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @Description: todo
 * @author: pangzhiwang
 * @create: 2018/4/4
 **/
@Configuration
public class DisruptorConfig {


    @Value("${swirly.strategy}")
    public  String  swirlyStrategy;

    @Bean(name="disruptor")
    public Disruptor<Order> disruptor(){

        Executor executor = Executors.newSingleThreadExecutor();

        orderEventFactory factory = new orderEventFactory();

        int bufferSize = 2048;

        // Construct the Disruptor
        Disruptor<Order> disruptor = new Disruptor<Order>(factory, bufferSize, executor);

        // Connect the handler
        disruptor.handleEventsWith(new EventHandler<Order>(){
            @Override
            public void onEvent(Order event, long sequence, boolean endOfBatch) throws Exception {
                MatchService.MatchStrategy matchStrategy = (MatchService.MatchStrategy)SpringContextSupport.getBean(swirlyStrategy);
                matchStrategy.match(event);
            }
        });
        disruptor.start();

        return disruptor;
    }

    @Bean(name="ringBuffer")
    public RingBuffer<Order> ringBuffer(@Qualifier("disruptor") Disruptor<Order> disruptor){
        return disruptor.getRingBuffer();
    }


}
