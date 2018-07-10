package com.viewfin.match.core.service.order;

import com.viewfin.match.core.exception.ErrorInfo;
import com.viewfin.match.core.entity.Order;
import com.viewfin.match.core.exception.Result;

import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;


/**
 * @Auther: pangzhiwang
 * @Date: 2018/7/4 16:20
 * @Description:
 */
public interface OrderService {

    /**
     * 创建订单
     * @param order
     * @param errorInfo
     */
    void createOrder(Order order, ErrorInfo errorInfo);


    /**
     * 撤销订单
     * @param orderId
     * @param errorInfo
     * @return
     */
    void cancelOrder(String orderId, ErrorInfo errorInfo);


    /**
     * 获取深度数据
     *
     * @param market
     * @param errorInfo
     * @return
     */
    public Result<Map<String,PriorityBlockingQueue<Order>>> getDeepData(String market, ErrorInfo errorInfo) ;
}
