package com.viewfin.match.core.component.disruptor;

import com.viewfin.match.core.entity.Order;

import java.util.Comparator;

/**
 * 买单比较器
 */
public class OrderBuyCompartor implements Comparator<Order> {

    //价格优先,然后时间优先,数量小优先
    public int compare(Order o1, Order o2) {
        if (o1.getTicks().compareTo(o2.getTicks()) > 0) {
            return -1;
        } else if (o1.getTicks().compareTo(o2.getTicks()) < 0) {
            return 1;
        } else {
            if (o1.getTimestamp().compareTo(o2.getTimestamp())>0) {
                return 1;
            } else if(o1.getTimestamp().compareTo(o2.getTimestamp())<0) {
                return -1;
            }else{
                if(o1.getLots().compareTo(o2.getLots()) > 0){
                    return 1;
                }if(o1.getLots().compareTo(o2.getLots())==0){
                    return 0;
                }else{
                    return -1;
                }
            }
        }
    }
}
