package com.nuoson.modulith.app.inventory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryDTO {
    private String productId;
    private String name;
    private Integer count;
}
