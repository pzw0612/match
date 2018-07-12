package com.viewfin.match.core.component.disruptor;

import com.lmax.disruptor.EventHandler;
import com.viewfin.match.core.entity.Order;
import com.viewfin.match.core.service.strategy.LimitMatchStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: pangzhiwang
 * @Date: 2018/7/11 20:58
 * @Description:
 */

@Component
public class OrderHandle implements EventHandler<Order> {

    @Autowired
    private LimitMatchStrategy limitMatchStrategy;

    @Override
    public void onEvent(Order event, long sequence, boolean endOfBatch) throws Exception {

        limitMatchStrategy.match(event);
    }
}
