package com.viewfin.match.core.service.strategy;

import com.viewfin.match.core.entity.Order;
import org.springframework.stereotype.Component;

/**
 * @Description: 市价撮合
 * @author: pangzhiwang
 * @create: 2018/4/6
 **/
@Component("marketMatchStrategy")
public class MarketMatchStrategy implements MatchService.MatchStrategy {
    @Override
    public void match(Order order) {

    }
}
