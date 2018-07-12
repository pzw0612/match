package com.viewfin.match.core.service.strategy;

import com.viewfin.match.core.entity.Order;
import com.viewfin.match.core.entity.Trade;
import com.viewfin.match.core.enums.OrderOpsEnum;
import com.viewfin.match.core.enums.OrderSideEnum;
import com.viewfin.match.core.enums.TradeStatusEnum;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description: 限价撮合
 * @author: pangzhiwang
 * @create: 2018/4/6
 **/

@Component("limitMatchStrategy")
public class LimitMatchStrategy extends AbstractMatchStrategy implements MatchService.MatchStrategy {


    private DefaultRedisScript<List> order_lt_match_Script;
    private DefaultRedisScript<List> order_gt_match_Script;
    private DefaultRedisScript<List> order_eq_match_Script;
    private DefaultRedisScript<List> cancel_order_Script;

    private Lock lock = new ReentrantLock();

    @PostConstruct
    public void init() {

        order_lt_match_Script = new DefaultRedisScript<List>();
        order_lt_match_Script.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/creatOrder_lt_match.lua")));
        order_lt_match_Script.setResultType(List.class);

        order_gt_match_Script = new DefaultRedisScript<List>();
        order_gt_match_Script.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/creatOrder_gt_match.lua")));
        order_gt_match_Script.setResultType(List.class);

        order_eq_match_Script = new DefaultRedisScript<List>();
        order_eq_match_Script.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/creatOrder_eq_match.lua")));
        order_eq_match_Script.setResultType(List.class);

        cancel_order_Script = new DefaultRedisScript<List>();
        cancel_order_Script.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/cancel_order.lua")));
        cancel_order_Script.setResultType(List.class);

    }

    @Override
    public void doBuy(Order taker) {

        Order maker = null;

        if (askQueue.size() == 0 || (maker = askQueue.poll()) == null) {
            //识别假单
            if (taker.getTimestamp().equals(new Long(-9999999L))) {
                return;
            } else {
                bidQueue.offer(taker);

            }
            return;
        }


        //订单是否已经取消
        if (cancelQueue.contains(maker.getOrderId())) {
            Trade trade = getCancelTrade(maker, getBidSide(taker.getSide()));

            cancelHandle(taker, maker, trade);

            return;
        }

        //未匹配上
        if (taker.getTicks().compareTo(maker.getTicks()) < 0) {
            //if(!bidQueue.contains(taker) && taker.getSide().equalsIgnoreCase(OrderOpsEnum.BUY.getCode())){
                bidQueue.offer(taker);
            //}
            //if(!askQueue.contains(maker) && maker.getSide().equalsIgnoreCase(OrderOpsEnum.SELL.getCode())){
                askQueue.offer(maker);
            //}
            return;
        }
        //匹配后处理
        matchPost(taker, maker);

    }


    @Override
    public void doSell(Order taker) {

        if (!taker.getSide().equalsIgnoreCase(OrderOpsEnum.SELL.getCode())) {
            LOGGER.error("doSell {}", taker.toString());
        }

        Order maker = null;

        if (bidQueue.size() == 0 || (maker = bidQueue.poll()) == null) {
            //需判断是否已经存在与买对列中
            //识别假单
            if (taker.getTimestamp().equals(new Long(-9999999L))) {
                return;
            } else {
                if(!askQueue.contains(taker) && taker.getSide().equalsIgnoreCase(OrderOpsEnum.SELL.getCode())){
                    askQueue.offer(taker);
                }
            }
            return;
        }

        //订单是否已经取消
        if (cancelQueue.contains(maker.getOrderId())) {
            Trade trade = getCancelTrade(maker, getBidSide(taker.getSide()));

            cancelHandle(taker, maker, trade);

            return;
        }
        if (taker.getTicks().compareTo(maker.getTicks()) > 0) {
            if(!askQueue.contains(taker) && taker.getSide().equalsIgnoreCase(OrderOpsEnum.SELL.getCode())){
                askQueue.offer(taker);
            }
            if(!bidQueue.contains(maker)&& maker.getSide().equalsIgnoreCase(OrderOpsEnum.BUY.getCode())){
                bidQueue.offer(maker);
            }
            return;
        }

        matchPost(taker, maker);

        return;

    }

    private boolean cancelHandle(Order taker, Order maker, Trade trade) {
        try {
            //发送撤销订单交易到kafka
            sendMsg(trade);

//            //redis 移除取消订单maker数据
//            listOperations.remove(orderConfig.getCancelOrderQueueMarket(),0,maker.getOrderId());
//
//            //redis 移除 createOrder 中 maker 数据
//            listOperations.remove(orderConfig.getCreareOrderQueueMarket(),0,maker.getOrderId());
//
//            ////redis 移除 createOrderDetail 中 maker 明细数据
//            hashOperations.getOperations().delete(orderConfig.getCreateOrderDetailMarket(maker.getOrderId()));



            List<String> keys = new ArrayList<>();
            keys.add(orderConfig.getCancelOrderQueueMarket());
            keys.add(orderConfig.getCreareOrderQueueMarket());
            keys.add(orderConfig.getCreateOrderDetailMarket(maker.getOrderId()));

            Long[] arr = new Long[1];
            arr[0] = maker.getOrderId();

            lock.lock();
            try {
                List resultList = (List) redisTemplate.execute(cancel_order_Script, keys, arr);

                if (resultList.size() < 3) {
                    LOGGER.warn("matchPost({}) error >: {}", "cancel_order.lua", "exec fail");
                }
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.warn("matchPost({}) error >: {}", "cancel_order.lua", e.getMessage());
            }finally {
                lock.unlock();
            }

            if (taker.getTimestamp().compareTo(-9999999L) == 0) {
                return true;
            } else {
                match(taker);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("cancelHandle({}) error >: {}", taker.toString(), e.getMessage());
        }
        return false;
    }

    private void matchPost(Order taker, Order maker) {
        //计算剩余额度
        Long lotsGap = taker.getLots() - maker.getLots();

        Trade trade = getNormorTrade(taker, maker, lotsGap);
        //发送撮合信息到kafka
        sendMsg(trade);

        //完全撮合
        if (lotsGap.compareTo(0l) == 0) {
            //redis createOrderList 中移除taker
//            listOperations.remove(orderConfig.getCreareOrderQueueMarket(),0,taker.getOrderId());
//            //redis createOrder detail 中移除taker
//            hashOperations.getOperations().delete(orderConfig.getCreateOrderDetailMarket(taker.getOrderId()));
//            //redis createOrderList 中移除maker
//            listOperations.remove(orderConfig.getCreareOrderQueueMarket(),0,maker.getOrderId());
//            //redis createOrder detail 中移除maker
//            hashOperations.getOperations().delete(orderConfig.getCreateOrderDetailMarket(maker.getOrderId()));

            List<String> keys = new ArrayList<>();
            keys.add(orderConfig.getCreareOrderQueueMarket());
            keys.add(orderConfig.getCreateOrderDetailMarket(taker.getOrderId()));
            keys.add(orderConfig.getCreateOrderDetailMarket(maker.getOrderId()));

            Long[] arr = new Long[2];
            arr[0] = taker.getOrderId();
            arr[1] = maker.getOrderId();

            doRedisScript(order_eq_match_Script,keys,arr,4);

            if(maker.getSide() !=null &&  maker.getSide().equals(OrderOpsEnum.BUY.getCode())){
                bidQueue.remove(maker);
                askQueue.remove(taker);
            }else{
                askQueue.remove(maker);
                bidQueue.remove(taker);
            }

            maker =null;
            taker=null;

            return;
        }
        if (lotsGap.compareTo(0l) > 0) {
            //更新redis中 taker 数量
//            hashOperations.put(orderConfig.getCreateOrderDetailMarket(taker.getOrderId()),"lots",lotsGap);
//
//            //redis createOrderList 中移除maker
//            listOperations.remove(orderConfig.getCreareOrderQueueMarket(),0,maker.getOrderId());
//
//            //redis createOrder detail 中移除maker
//            hashOperations.getOperations().delete(orderConfig.getCreateOrderDetailMarket(maker.getOrderId()));


            List<String> keys = new ArrayList<>();
            keys.add(orderConfig.getCreateOrderDetailMarket(taker.getOrderId()));
            keys.add(orderConfig.getCreareOrderQueueMarket());
            keys.add(orderConfig.getCreateOrderDetailMarket(maker.getOrderId()));

            Long[] arr = new Long[2];
            arr[0] = lotsGap;
            arr[1] = maker.getOrderId();
//            try {
//                List resultList = (List) redisTemplate.execute(order_gt_match_Script, keys, arr);
//                if (resultList.size() < 3) {
//                    LOGGER.warn("matchPost({}) error >: {}", "order_gt_match_Script.lua", "exec fail");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                //@TODO
//                LOGGER.warn("matchPost({}) error >: {}", "order_gt_match_Script.lua", e.getMessage());
//
//            }

            doRedisScript(order_gt_match_Script,keys,arr,3);

            //更新 taker 数量
            taker.setLots(lotsGap);

            // 加速GC
            if(maker.getSide() !=null && maker.getSide().equals(OrderOpsEnum.BUY.getCode())){
                bidQueue.remove(maker);
            }else{
                askQueue.remove(maker);
            }
            maker=null;


            match(taker);

            return;
        } else {
//            //redis createOrderList 中移除taker
//            listOperations.remove(orderConfig.getCreareOrderQueueMarket(),0,taker.getOrderId());
//            //redis createOrder detail 中移除taker
//            hashOperations.getOperations().delete(orderConfig.getCreateOrderDetailMarket(taker.getOrderId()));
//
//            //更新redis中 maker 数量
//            hashOperations.put(orderConfig.getCreateOrderDetailMarket(maker.getOrderId()),"lots",-lotsGap);

            List<String> keys = new ArrayList<>();
            keys.add(orderConfig.getCreareOrderQueueMarket());
            keys.add(orderConfig.getCreateOrderDetailMarket(taker.getOrderId()));
            keys.add(orderConfig.getCreateOrderDetailMarket(maker.getOrderId()));

            Long[] arr = new Long[2];
            arr[0] = taker.getOrderId();
            arr[1] = -lotsGap;

//            try {
//                List resultList = (List) redisTemplate.execute(order_lt_match_Script, keys, arr);
//                if (resultList.size() < 3) {
//                    LOGGER.warn("matchPost({}) error >: {}", "order_gt_match_Script.lua", "exec fail");
//                }
//            } catch (Exception e) {
//                //@TODO
//                LOGGER.warn("matchPost({}) error >: {}", "order_gt_match_Script.lua", e.getMessage());
//                e.printStackTrace();
//            }

            doRedisScript(order_lt_match_Script,keys,arr,3);

            maker.setLots(-lotsGap);

            if(taker.getSide() !=null && taker.getSide().equals(OrderOpsEnum.BUY.getCode())){
                bidQueue.remove(taker);
            }else{
                askQueue.remove(taker);
            }

            taker=null;

            match(maker);

            return;
        }
    }

    private Trade getNormorTrade(Order taker, Order maker, Long lotsGap) {
        Trade trade = new Trade();
        trade.setMatchId(genTradeId());
        if (OrderOpsEnum.BUY.getCode().equalsIgnoreCase(taker.getSide())) {
            trade.setBidOrderId(taker.getOrderId());
            trade.setAskOrderId(maker.getOrderId());
            trade.setSide(OrderSideEnum.BID.getCode());
            trade.setTicks(maker.getTicks());
        } else if (OrderOpsEnum.SELL.getCode().equalsIgnoreCase(taker.getSide())){
            trade.setBidOrderId(maker.getOrderId());
            trade.setAskOrderId(taker.getOrderId());
            trade.setSide(OrderSideEnum.ASK.getCode());
            trade.setTicks(taker.getTicks());
        }
        trade.setMarket(maker.getMarket());
        if (lotsGap >= 0) {
            trade.setLots(maker.getLots());
        } else {
            trade.setLots(taker.getLots());
        }
        trade.setTime(System.currentTimeMillis());
        trade.setStatus(TradeStatusEnum.trade.getCode());
        return trade;
    }

    private String getBidSide(String takeSide) {
        if (OrderOpsEnum.BUY.getCode().equalsIgnoreCase(takeSide)) {
            return OrderSideEnum.BID.getCode();
        } else  if (OrderOpsEnum.SELL.getCode().equalsIgnoreCase(takeSide)) {
            return OrderSideEnum.ASK.getCode();
        }else{
            return "unknow";
        }
    }

    private Trade getCancelTrade(Order maker, String side) {
        Trade trade = new Trade();
        trade.setMarket(maker.getMarket());
        trade.setMatchId(genTradeId());
        if (OrderOpsEnum.BUY.getCode().equalsIgnoreCase(maker.getSide())) {
            trade.setAskOrderId(-1L);
            trade.setBidOrderId(maker.getOrderId());
        } else {
            trade.setAskOrderId(maker.getOrderId());
            trade.setBidOrderId(-1L);
        }
        trade.setLots(maker.getLots());
        trade.setTicks(maker.getTicks());
        trade.setSide(side);
        trade.setTime(System.currentTimeMillis());
        trade.setStatus(TradeStatusEnum.cancel.getCode());
        return trade;
    }

    private  void doRedisScript(DefaultRedisScript<List> list ,List<String> keys,Long[] arr,int resultSize){
        lock.lock();
        try {
            List resultList = (List) redisTemplate.execute(list, keys, arr);
            if (resultList.size() < resultSize) {
                LOGGER.warn("matchPost({}) error >: {}", list.getScriptAsString(), "exec fail");
            }
        } catch (Exception e) {
            //@TODO
            LOGGER.warn("matchPost({}) error >: {}", list.getScriptAsString(), e.getMessage());
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

}