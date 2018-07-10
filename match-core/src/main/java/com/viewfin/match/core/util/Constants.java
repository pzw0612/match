package com.viewfin.match.core.util;

import org.springframework.beans.factory.annotation.Value;

/**
 * @Description: todo
 * @author: pangzhiwang
 * @create: 2018/4/3
 **/
public class Constants {

    //--------------------redis------------------------------
    //撮合待处理订单
    @Value("${swirly.redis.hash.order.req}")
    public  static  String swirly_order_req;

    //订单撤销
    @Value("${swirly.redis.hash.order.cancle.req}")
    public  static  String swirly_order_cancle_req;


    //撮合交易id
    @Value("${swirly.redis.key.trade.id}")
    public  static  String swirly_trade_id;

    //撮合交易信息
    @Value("${swirly.redis.hash.result.detail}")
    public  static  String swirly_result_detail;

    //撮合交易信息
    @Value("${swirly.redis.list.result.list}")
    public  static  String swirly_result_list;


    //--------------------kafka------------------------------
    @Value("${swirly.kafka.producer.to.order.topic}")
    public static  String  swirly_kafka_producer_to_order_topic;


    //-----------------------订单系统--------------------------
    @Value("${swirly.order.notCompleteOrderList.url}")
    public static  String swirly_order_notCompleteOrderList_url;
}
