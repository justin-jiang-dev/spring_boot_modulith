package com.nuoson.modulith.infra.order;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.nuoson.modulith.domain.order.OrderRedisGateway;
import com.nuoson.modulith.domain.ordermodel.OrderEntity;
import com.nuoson.modulith.infra.utils.JsonUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderRedisGatewayImpl implements OrderRedisGateway {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public OrderEntity getById(String orderId) {
        String entityJson = stringRedisTemplate.opsForValue().get(orderId);
        if (StringUtils.isBlank(entityJson)) {
            return null;
        }
        return JsonUtils.from(entityJson, OrderEntity.class);
    }

    @Override
    public void save(OrderEntity orderEntity) {
        stringRedisTemplate.opsForValue().set(orderEntity.getOrderId(), JsonUtils.to(orderEntity));
    }
}
