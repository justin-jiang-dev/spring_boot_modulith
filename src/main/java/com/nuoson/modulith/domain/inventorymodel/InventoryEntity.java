package com.nuoson.modulith.domain.inventorymodel;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 
 */
@Data
public class InventoryEntity {
    @NotBlank
    private String productId;
    private String name;
    private Integer count;
}
