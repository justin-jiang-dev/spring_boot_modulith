package com.nuoson.modulith.infra.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.nuoson.modulith.domain.order.OrderRedisGateway;
import com.nuoson.modulith.domain.ordermodel.OrderEntity;
import com.nuoson.modulith.infra.utils.JsonUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderRedisGatewayImpl implements OrderRedisGateway {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public OrderEntity getById(String orderId) {
        return null;
    }

    @Override
    public void create(OrderEntity orderEntity) {
        stringRedisTemplate.opsForValue().set(orderEntity.getOrderId(), JsonUtils.to(orderEntity));
    }
}
