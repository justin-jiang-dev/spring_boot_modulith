package com.nuoson.modulith.infra.order;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.nuoson.modulith.domain.foundationmodel.ApiErrorCode;
import com.nuoson.modulith.domain.foundationmodel.BusinessException;
import com.nuoson.modulith.domain.order.OrderRepositoryGateway;
import com.nuoson.modulith.domain.ordermodel.OrderEntity;
import com.nuoson.modulith.infra.internal.inventory.repository.model.InventoryDO;
import com.nuoson.modulith.infra.internal.inventory.repository.service.InventoryDatabaseService;
import com.nuoson.modulith.infra.internal.order.repository.mapping.OrderDOMapping;
import com.nuoson.modulith.infra.internal.order.repository.model.ProductOrderDO;
import com.nuoson.modulith.infra.internal.order.repository.service.ProductOrderDatabaseService;

import lombok.RequiredArgsConstructor;

@DS("secondaryH2")
@Service
@RequiredArgsConstructor
public class OrderRepositoryGatewayImpl implements OrderRepositoryGateway {
    private final ProductOrderDatabaseService orderDBService;
    private final InventoryDatabaseService inventoryDBService;
    private final OrderDOMapping orderDOMapping;

    @Cacheable(value = "order", key = "#orderId")
    @Override
    public OrderEntity getById(String orderId) {
        ProductOrderDO doObj = orderDBService.lambdaQuery()
                .eq(ProductOrderDO::getOrderId, orderId)
                .one();
        return orderDOMapping.toEntity(doObj);
    }

    /**
     * 创建订单
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderEntity save(OrderEntity entity) {
        InventoryDO inventoryObj = inventoryDBService.lambdaQuery()
                .eq(InventoryDO::getProductId, entity.getProductId())
                .one();
        if (inventoryObj == null) {
            throw new BusinessException(
                    ApiErrorCode.OrderErrorCodeEnum.PRODUCT_MISSING,
                    String.format("ProductId: %s", entity.getProductId()));
        }
        if (inventoryObj.getCount() < entity.getCount()) {
            throw new BusinessException(
                    ApiErrorCode.OrderErrorCodeEnum.PRODUCT_COUNT_NOT_ENOUGH,
                    String.format("ProductId: %s", entity.getProductId()));
        }
        // 使用 productId + count 作为条件，实现 CAS 更新方式，避免使用悲观锁
        LambdaUpdateWrapper<InventoryDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(InventoryDO::getProductId, entity.getProductId())
                .eq(InventoryDO::getCount, inventoryObj.getCount())
                .set(InventoryDO::getCount, inventoryObj.getCount() - entity.getCount());
        if (!inventoryDBService.update(updateWrapper)) {
            throw new BusinessException(
                    ApiErrorCode.OrderErrorCodeEnum.DB_CAS_UPDATE_FAILED,
                    String.format("ProductId: %s, expected count in inventory: %d, count in order: %d ",
                            inventoryObj.getProductId(), inventoryObj.getCount(), entity.getCount()));
        }
        if (!orderDBService.save(orderDOMapping.fromEntity(entity))) {
            throw new BusinessException(ApiErrorCode.OrderErrorCodeEnum.UNKNOWN);
        }

        return entity;
    }
}
