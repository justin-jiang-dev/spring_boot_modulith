package com.nuoson.modulith.domain.inventory;

import java.util.List;

import org.springframework.validation.annotation.Validated;

import com.nuoson.modulith.domain.inventorymodel.InventoryEntity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * InventoryRepositoryGateway 被 infra 层 InventoryRepositoryGatewayImpl 实现 <br/>
 * Domain 层通过对 InventoryRepositoryGateway 的引用，调用 infra层的接口实现，实现 domain 层和 infra
 * 层的解耦
 */
@Validated
public interface InventoryRepositoryGateway {
    /**
     * 根据指定的库存清单 id, 返回对应的实例
     * 
     * @param productId -- 库存产品 id （参见 product_id 列）
     * @return
     */
    InventoryEntity getById(@NotBlank String productId);

    /**
     * 根据库存量查询产品清单 <br/>
     * 注： <br/>
     * 1. 配合类上的 @Validated 注解使用， @NotNull 注解验证返回结果不能为空 <br/>
     * 2. 返回值集合 的内部元素，可以通过 @Valid 注解触发元素的验证
     * 
     * @param from
     * @param to
     * @return
     */
    @NotNull
    List<@Valid InventoryEntity> queryByCount(Long from, Long to);
}
