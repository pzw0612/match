package com.viewfin.match.core.conf;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.viewfin.match.core.component.disruptor.OrderHandle;
import com.viewfin.match.core.entity.Order;
import com.viewfin.match.core.component.disruptor.OrderEventFactory;
import com.viewfin.match.core.service.strategy.MatchService;
import com.viewfin.match.core.component.spring.SpringContextSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @Description: todo
 * @author: pangzhiwang
 * @create: 2018/4/4
 **/
@Configuration
public class DisruptorConfig {

    @Autowired
    private OrderHandle orderHandle;

    @Bean(name="disruptor")
    public Disruptor<Order> disruptor(){

        //Executor executor = Executors.newSingleThreadExecutor();

        // 生产者的线程工厂
        ThreadFactory threadFactory = new ThreadFactory(){
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "disruptor simpleThread");
            }
        };

        OrderEventFactory factory = new OrderEventFactory();

        int bufferSize = 1024;

        // 阻塞策略 自旋 + yield + 自旋
        YieldingWaitStrategy strategy = new YieldingWaitStrategy();

        // Construct the Disruptor
        //Disruptor<Order> disruptor = new Disruptor<Order>(factory, bufferSize, executor);

        Disruptor<Order> disruptor = new Disruptor(factory, bufferSize, threadFactory, ProducerType.SINGLE, strategy);

        // Connect the handler
        disruptor.handleEventsWith(orderHandle);

        disruptor.start();

        return disruptor;
    }

    @Bean(name="ringBuffer")
    public RingBuffer<Order> ringBuffer(@Qualifier("disruptor") Disruptor<Order> disruptor){
        return disruptor.getRingBuffer();
    }


}
