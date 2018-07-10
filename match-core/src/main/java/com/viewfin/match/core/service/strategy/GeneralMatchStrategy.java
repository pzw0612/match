package com.viewfin.match.core.service.strategy;

import com.viewfin.match.core.entity.Order;
import org.springframework.stereotype.Component;

/**
 * @Description: 单向策略，即只有buy方价格大于等于sell方价格才能执行
 * @author: pangzhiwang
 * @create: 2018/4/5
 **/
@Component("generalMatchStrategy")
public class GeneralMatchStrategy implements MatchService.MatchStrategy {
    @Override
    public void match(Order order) {

    }
}
