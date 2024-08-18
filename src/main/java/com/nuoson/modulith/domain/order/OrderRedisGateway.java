package com.nuoson.modulith.domain.order;

import com.nuoson.modulith.domain.ordermodel.OrderEntity;

public interface OrderRedisGateway {
    /**
     * 根据指定的订单 id, 返回对应的实例
     * 
     * @param orderId -- 订单 id
     * @return
     */
    OrderEntity getById(String orderId);

    /**
     * 生成新订单
     * 
     * @param orderEntity -- 订单实例
     */
    void create(OrderEntity orderEntity);
}
