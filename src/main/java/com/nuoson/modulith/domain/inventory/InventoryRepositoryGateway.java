package com.nuoson.modulith.domain.inventory;

import java.util.List;

import org.springframework.validation.annotation.Validated;

import com.nuoson.modulith.domain.inventorymodel.InventoryEntity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Validated
public interface InventoryRepositoryGateway {
    /**
     * 根据指定的库存清单 id, 返回对应的实例
     * 
     * @param productId -- 库存产品 id （参见 product_id 列）
     * @return
     */
    InventoryEntity getById(String productId);

    /**
     * 根据库存量查询产品清单
     * 
     * @param from
     * @param to
     * @return
     */
    @NotNull
    List<@Valid InventoryEntity> queryByCount(Long from, Long to);
}
