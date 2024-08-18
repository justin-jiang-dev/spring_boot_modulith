package com.nuoson.modulith.app.order;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nuoson.modulith.app.foundationmodel.BasicResultDTO;
import com.nuoson.modulith.domain.order.OrderRedisGateway;
import com.nuoson.modulith.domain.ordermodel.OrderEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderExecutor {
    private final OrderRedisGateway orderRedisGateway;
    private final OrderDTOMapping orderDTOMapping;

    public BasicResultDTO<OrderDTO> create(OrderCreationCommand command) {
        OrderEntity entity = new OrderEntity();
        entity.setCount(command.getCount());
        entity.setName(command.getProductName());
        entity.setProductId(command.getProductId());
        entity.setOrderId(UUID.randomUUID().toString().replace("-", ""));
        orderRedisGateway.create(entity);
        return BasicResultDTO.success(orderDTOMapping.fromEntity(entity));
    }
}
