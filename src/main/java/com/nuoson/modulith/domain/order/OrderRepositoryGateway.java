package com.nuoson.modulith.domain.order;

import org.springframework.validation.annotation.Validated;

import com.nuoson.modulith.domain.ordermodel.OrderEntity;

import jakarta.validation.constraints.NotBlank;

/**
 * OrderRepositoryGateway 接口，被 infra 层 OrderRepositoryGatewayImpl 实现。<br/>
 * Domain 层通过对 InventoryRepositoryGateway 的引用，调用 infra层的接口实现，实现 domain 层和 infra
 * 层的解耦
 */
@Validated
public interface OrderRepositoryGateway {

    /**
     * 根据指定订单id，返回订单实例
     * 
     * @param orderId
     * @return
     */
    OrderEntity getById(@NotBlank String orderId);

    /**
     * 创建订单
     * 
     * @param orderEntity
     * @return
     */
    OrderEntity save(OrderEntity orderEntity);
}
