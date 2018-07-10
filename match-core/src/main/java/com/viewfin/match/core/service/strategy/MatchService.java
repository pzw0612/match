package com.viewfin.match.core.service.strategy;

import com.viewfin.match.core.entity.Order;

/**
 * @Auther: pangzhiwang
 * @Date: 2018/7/4 16:08
 * @Description:
 */
public interface MatchService {
    /**
     * @Description: todo
     * @author: pangzhiwang
     * @create: 2018/4/5
     **/
    interface MatchStrategy {

        public void match(Order order);


    }

    /**
     * @Description: 双向撮合策略
     *
     * 1。 当tacker方(buy) > maker方(sell)，满足撮合条件
     * 2。 当tacker方(sell) > maker方(buy)，满足撮合条件
     *
     * @author: pangzhiwang
     * @create: 2018/4/5
     **/

    class TwowayMatchStrategy implements MatchStrategy {
        private MatchStrategy matchStrategy;
        @Override
        public void match(Order order) {
            matchStrategy.match(order);
        }
    }
}
