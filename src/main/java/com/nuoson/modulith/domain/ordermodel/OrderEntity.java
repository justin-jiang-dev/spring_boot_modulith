package com.nuoson.modulith.domain.ordermodel;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderEntity {
    @NotBlank
    private String orderId;
    @NotBlank
    private String productId;
    private String name;
    private Integer count;
}
