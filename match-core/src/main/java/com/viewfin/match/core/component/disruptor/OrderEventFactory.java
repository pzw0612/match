package com.viewfin.match.core.component.disruptor;

import com.lmax.disruptor.EventFactory;
import com.viewfin.match.core.entity.Order;

public class OrderEventFactory implements EventFactory<Order>
{
    public Order newInstance()
    {
        return new Order();
    }
}
