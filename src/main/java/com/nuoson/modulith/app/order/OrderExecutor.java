package com.nuoson.modulith.app.order;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nuoson.modulith.app.foundationmodel.BasicResultDTO;
import com.nuoson.modulith.domain.order.OrderRepositoryGateway;
import com.nuoson.modulith.domain.ordermodel.OrderEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderExecutor {
    private final OrderRepositoryGateway orderRepositoryGateway;
    private final OrderDTOMapping orderDTOMapping;

    public BasicResultDTO<OrderDTO> save(OrderCreationCommand command) {
        OrderEntity entity = new OrderEntity();
        entity.setCount(command.getCount());
        entity.setProductId(command.getProductId());
        entity.setOrderId(UUID.randomUUID().toString().replace("-", ""));
        orderRepositoryGateway.save(entity);
        return BasicResultDTO.success(orderDTOMapping.fromEntity(entity));
    }

    public BasicResultDTO<OrderDTO> getById(String orderId) {
        return BasicResultDTO.success(orderDTOMapping.fromEntity(orderRepositoryGateway.getById(orderId)));
    }
}
