package com.viewfin.match.core.component.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.viewfin.match.core.entity.Order;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderProducer
{
    @Autowired
    private  RingBuffer<Order> ringBuffer;

    public void onData(Order order)
    {
        long sequence = ringBuffer.next();  // Grab the next sequence
        try
        {
            Order event = ringBuffer.get(sequence); // Get the entry in the Disruptor
            BeanUtils.copyProperties(event,order);

            ringBuffer.publish(sequence);

        } catch (Exception e) {
           //log.error("order复制异常：order："+ order.toString());
            //TODO  异常的订单请求放入到数据库 ，有空处理
            //System.exit(1);
        }
    }
}
