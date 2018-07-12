package com.viewfin.match.bcheth;

import com.lmax.disruptor.dsl.Disruptor;
import com.viewfin.match.core.component.spring.SpringContextSupport;
import com.viewfin.match.core.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.TimeUnit;

/**
 * @Description: todo
 * @author: pangzhiwang
 * @create: 2018/4/1
 **/
@EnableScheduling
@SpringBootApplication
@EnableAutoConfiguration
@Slf4j
@ComponentScan("com.viewfin.match.core")
public class BchethMatchApplication {

    public static Disruptor<Order> disruptor;

    public static void main(String[] args) {
        try {
            SpringApplication application = new SpringApplication(BchethMatchApplication.class);
            application.addListeners(new ApplicationListener<ApplicationReadyEvent>() {
                                         @Override
                                         public void onApplicationEvent(ApplicationReadyEvent event) {
                                             try {
                                                 ConfigurableApplicationContext context = event.getApplicationContext();
                                                 SpringContextSupport.setApplicationContext(context);
                                                 disruptor = context.getBean(Disruptor.class);

                                             } catch (Exception e) {
                                                 log.error("system start error,error:> {}", e.getMessage());
                                                 e.printStackTrace();
                                             }
                                         }
                                     }, new ApplicationListener<ContextClosedEvent>() {
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
        } catch (Exception e) {
            log.error("main error,msg: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
