package com.viewfin.match.core.entity;



import com.viewfin.match.core.util.JSONUtil;

import java.io.Serializable;
import java.util.Objects;


/**
 * @Auther: pangzhiwang
 * @Date: 2018/7/4 06:30
 * @Description:
 */
public class Order {

    private Long trader;//用户
    private Long orderId;//订单id
    private String market;//交易对
    private String side;//买卖方向(1:BUY,2:SELL)
    private Long lots;//数量
    private Long ticks;//价格
    private String type;//类型（1:LIMIT、2:MARKET）
    private Long timestamp;//类型（1:LIMIT、2:MARKET）

    public Long getTrader() {
        return trader;
    }

    public void setTrader(Long trader) {
        this.trader = trader;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public Long getLots() {
        return lots;
    }

    public void setLots(Long lots) {
        this.lots = lots;
    }

    public Long getTicks() {
        return ticks;
    }

    public void setTicks(Long ticks) {
        this.ticks = ticks;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return
                Objects.equals(orderId, order.orderId) &&
                Objects.equals(market, order.market) &&
                Objects.equals(side, order.side) &&
                Objects.equals(type, order.type) &&
                Objects.equals(timestamp, order.timestamp);
    }

    @Override
    public int hashCode() {

        return Objects.hash(orderId, market, side, type, timestamp);
    }

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }

}
