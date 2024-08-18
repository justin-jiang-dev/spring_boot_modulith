package com.nuoson.modulith.infra.internal.inventory.repository.mapping;

import org.mapstruct.Mapper;

import com.nuoson.modulith.domain.inventorymodel.InventoryEntity;
import com.nuoson.modulith.infra.internal.inventory.repository.model.InventoryDO;

@Mapper(componentModel = "spring")
@SuppressWarnings("PMD.ClassNamingShouldBeCamelRule")
public interface InventoryDOMapping {
    /**
     * from do to entity
     * 
     * @param doObj
     * @return
     */
    InventoryEntity toEntity(InventoryDO doObj);
}
