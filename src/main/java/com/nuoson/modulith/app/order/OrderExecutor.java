package com.nuoson.modulith.app.order;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nuoson.modulith.app.foundationmodel.BasicResultDTO;
import com.nuoson.modulith.domain.order.OrderRedisGateway;
import com.nuoson.modulith.domain.order.OrderRepositoryGateway;
import com.nuoson.modulith.domain.ordermodel.OrderEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 订单业务执行器，负责调用 domain 层的接口，并将结果转换为 DTO
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderExecutor {
    /**
     * 订单存储 Gateway
     */
    private final OrderRepositoryGateway orderRepositoryGateway;
    private final OrderRedisGateway orderRedisGateway;
    private final OrderDTOMapping orderDTOMapping;

    /**
     * 保存新订单 <br/>
     * 提供 Database 和 Redis 访问用例
     * 
     * @param command
     * @return
     */
    public BasicResultDTO<OrderDTO> save(OrderCreationCommand command) {
        OrderEntity entity = new OrderEntity();
        entity.setCount(command.getCount());
        entity.setProductId(command.getProductId());
        entity.setOrderId(UUID.randomUUID().toString().replace("-", ""));
        OrderEntity resultEntity = orderRepositoryGateway.save(entity);
        return BasicResultDTO.success(orderDTOMapping.fromEntity(resultEntity));
    }

    /**
     * 
     * @param orderId
     * @return
     */
    public BasicResultDTO<OrderDTO> getById(String orderId) {
        OrderEntity orderEntity = orderRedisGateway.getById(orderId);
        if (orderEntity == null) {
            log.debug("orderId: {} is not in redis cache", orderId);
            orderEntity = orderRepositoryGateway.getById(orderId);
            orderRedisGateway.save(orderEntity);
        }

        return BasicResultDTO.success(orderDTOMapping.fromEntity(orderEntity));
    }
}
