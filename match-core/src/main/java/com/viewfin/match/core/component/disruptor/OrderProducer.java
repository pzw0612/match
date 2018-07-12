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

            event.setTicks(order.getTicks());
            event.setTimestamp(order.getTimestamp());
            event.setLots(order.getLots());
            event.setOrderId(order.getOrderId());

            event.setSide(order.getSide());

            event.setType(order.getType());
            event.setMarket(order.getMarket());
            event.setTrader(order.getTrader());


        }finally {
            ringBuffer.publish(sequence);
        }
    }
}
