package com.viewfin.match.core.entity;

import com.viewfin.match.core.util.JSONUtil;


import java.io.Serializable;

/**
 * @Auther: pangzhiwang
 * @Date: 2018/7/4 06:35
 * @Description:
 */
public class Trade implements Serializable {


    private String market;//市场
    private Long matchId;//撮合id
    private Long askOrderId;//卖方orderid
    private Long bidOrderId;//买方orderId
    private Long lots;//数量
    private Long ticks;//价格
    private String side;//taker买卖方向（1:bid,2:ask),
    private Long time;//撮合时间
    private String status; //trade/cancle


    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Long getAskOrderId() {
        return askOrderId;
    }

    public void setAskOrderId(Long askOrderId) {
        this.askOrderId = askOrderId;
    }

    public Long getBidOrderId() {
        return bidOrderId;
    }

    public void setBidOrderId(Long bidOrderId) {
        this.bidOrderId = bidOrderId;
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

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}
