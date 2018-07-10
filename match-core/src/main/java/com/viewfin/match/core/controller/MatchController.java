package com.viewfin.match.core.controller;

import com.viewfin.match.core.conf.OrderConfig;
import com.viewfin.match.core.enums.OrderSideEnum;
import com.viewfin.match.core.enums.OrderTypeEnum;
import com.viewfin.match.core.exception.ErrorInfo;
import com.viewfin.match.core.entity.Order;
import com.viewfin.match.core.enums.OrderOpsEnum;
import com.viewfin.match.core.enums.StatusCodeEnum;
import com.viewfin.match.core.exception.Result;
import com.viewfin.match.core.service.order.OrderService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @Auther: pangzhiwang
 * @Date: 2018/7/4 11:25
 * @Description:
 */

@RestController
@ComponentScan("com.viewfin.match.core")
@RequestMapping("/match")
public class MatchController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderConfig orderConfig;


    @Autowired
    private ValueOperations<String, Order> valueOperations;


    @ResponseBody
    @RequestMapping(value="/createOrder/{market}")
    public Result<String> createOrder(@PathVariable("market") String market,@RequestBody Order order) {
        Result<String> result = null;
        if(order==null){
            result = Result.error(StatusCodeEnum.bad_request.getCode(),"request para can't null");
            return result;
        }
        createOrderParaValidate(order,result);

        if(result != null){
            return result;
        }
        if(!order.getMarket().equalsIgnoreCase(market) ||
        !order.getMarket().equalsIgnoreCase(orderConfig.getMarket())){
            result = Result.error(StatusCodeEnum.bad_request.getCode(),"getway wrong");
            return result;
        }
        if(!(OrderOpsEnum.BUY.getCode().equalsIgnoreCase(order.getSide() ) ||
                OrderOpsEnum.SELL.getCode().equalsIgnoreCase(order.getSide() ))){
            result = Result.error(StatusCodeEnum.bad_request.getCode(),"order side wrong");
            return result;
        }

        try {
            //订单重复检查
            String redisOrderDetailKey = orderConfig.getCreateOrderDetailMarket(order.getOrderId());
            if(valueOperations.getOperations().hasKey(redisOrderDetailKey)){
                result = Result.error(StatusCodeEnum.bad_request.getCode(),"orderId(" +order.getOrderId() +") already exists");
                return result;
            }

            ErrorInfo errorInfo =null;
            orderService.createOrder(order,errorInfo);
            if(errorInfo!=null && StatusCodeEnum.SUCCESS.getCode()!=errorInfo.getCode()){
                result = Result.error(StatusCodeEnum.internal_server_error.getCode(), errorInfo.getMsg());
                return result;
            }
            result= Result.success();
            return  result;

        } catch (Exception e) {
            result = Result.error(StatusCodeEnum.internal_server_error.getCode(),e.getMessage());
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value="/cancelOrder/{market}/{orderId}")
    public Result<String> cancelOrder(@PathVariable("market") String market, @PathVariable String orderId) {

        Result<String> result = new Result<>();
        ErrorInfo errorInfo =null;

        if(StringUtils.isEmpty(orderId) || !NumberUtils.isDigits(orderId) ){
            result = Result.error(StatusCodeEnum.bad_request.getCode(),"request orderid require");
            return result;
        }
        if(StringUtils.isEmpty(market)){
            result = Result.error(StatusCodeEnum.bad_request.getCode(),"request market require");
            return result;
        }
        if(!market.equalsIgnoreCase(orderConfig.getMarket())){
            result = Result.error(StatusCodeEnum.bad_request.getCode(),"getway wrong");
            return result;
        }

        try {
            orderService.cancelOrder(orderId,errorInfo);
            if(errorInfo !=null && errorInfo.isFailed()){
                result = Result.error(StatusCodeEnum.internal_server_error.getCode(), errorInfo.getMsg());
                return result;
            }
            result= Result.success();
            return  result;
        } catch (Exception e) {
            result = Result.error(StatusCodeEnum.internal_server_error.getCode(),e.getMessage());
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value="/getDeepData/{market}",method = RequestMethod.GET)
    public Result<Map<String,PriorityBlockingQueue<Order>>> getDeepData(@PathVariable("market") String market) {
        Result<Map<String,PriorityBlockingQueue<Order>>> result ;
        try {
            ErrorInfo errorInfo =null;
            if(StringUtils.isEmpty(market)){
                result = Result.error(StatusCodeEnum.bad_request.getCode(),"request market require");
                return result;
            }
            if(!market.equalsIgnoreCase(orderConfig.getMarket())){
                result = Result.error(StatusCodeEnum.bad_request.getCode(),"getway wrong");
                return result;
            }
            result = orderService.getDeepData(market,errorInfo);
            if(errorInfo!=null && errorInfo.isFailed()){
                result = Result.error(StatusCodeEnum.internal_server_error.getCode(),errorInfo.getMsg());
                return result;
            }
            return  result;
        } catch (Exception e) {

            result = Result.error(StatusCodeEnum.internal_server_error.getCode(),e.getMessage());
            return result;
        }
    }

    public void createOrderParaValidate(Order order,Result<String> result){
         if(StringUtils.isEmpty(order.getMarket())){
             result = Result.error(StatusCodeEnum.bad_request.getCode(),"request market require");
             return;
         }
        if(StringUtils.isEmpty(order.getSide()) || OrderSideEnum.getEnum(order.getSide())==null){
            result = Result.error(StatusCodeEnum.bad_request.getCode(),"request side require");
            return;
        }
        if(StringUtils.isEmpty(order.getType()) || OrderTypeEnum.getEnum(order.getType())==null){
            result = Result.error(StatusCodeEnum.bad_request.getCode(),"request type require");
            return;
        }
        if(order.getOrderId()==null || order.getOrderId().longValue() <=0){
            result = Result.error(StatusCodeEnum.bad_request.getCode(),"request orderid require");
            return;
        }
        if(order.getLots()==null || order.getLots().longValue() <=0){
            result = Result.error(StatusCodeEnum.bad_request.getCode(),"request lots require");
            return;
        }

        if(order.getTicks()==null|| order.getTicks().longValue() <=0){
            result = Result.error(StatusCodeEnum.bad_request.getCode(),"request ticks require");
            return;
        }
        if(order.getTimestamp()==null|| order.getTimestamp().longValue() <=0){
            result = Result.error(StatusCodeEnum.bad_request.getCode(),"request timestamp require");
            return;
        }
    }

}
