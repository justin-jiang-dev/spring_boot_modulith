package com.nuoson.modulith.app.inventory;

import java.util.List;

import org.mapstruct.Mapper;

import com.nuoson.modulith.domain.inventorymodel.InventoryEntity;

@Mapper(componentModel = "spring")
@SuppressWarnings("PMD.ClassNamingShouldBeCamelRule")
public interface InventoryDTOMapping {

    /**
     * from entity to dto
     * 
     * @param entity
     * @return
     */
    InventoryDTO fromEntity(InventoryEntity entity);

    /**
     * from entity list to dto list
     * 
     * @param entity
     * @return
     */
    List<InventoryDTO> fromEntity(List<InventoryEntity> entity);
}
