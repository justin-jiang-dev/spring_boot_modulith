package com.nuoson.modulith.infra.inventory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.nuoson.modulith.domain.inventory.InventoryRepositoryGateway;
import com.nuoson.modulith.domain.inventorymodel.InventoryEntity;
import com.nuoson.modulith.infra.internal.inventory.repository.mapping.InventoryDOMapping;
import com.nuoson.modulith.infra.internal.inventory.repository.model.InventoryDO;
import com.nuoson.modulith.infra.internal.inventory.repository.service.InventoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryRepositoryGatewayImpl implements InventoryRepositoryGateway {
    private final InventoryService inventoryService;
    private final InventoryDOMapping inventoryDOMapping;

    @Cacheable(value = "inventory", key = "#productId")
    @Override
    public InventoryEntity getById(String productId) {
        InventoryDO doObj = inventoryService.lambdaQuery()
                .eq(InventoryDO::getProductId, productId)
                .one();
        return inventoryDOMapping.toEntity(doObj);
    }

    @Override
    public List<InventoryEntity> queryByCount(Long from, Long to) {
        List<InventoryEntity> result = new ArrayList<>();
        result.add(new InventoryEntity());
        return result;
    }
}
