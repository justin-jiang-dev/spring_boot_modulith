package com.nuoson.modulith.app.order;

import lombok.Data;

@Data
public class OrderDTO {
    private String orderId;
    private String productId;
    private String name;
    private Integer count;
}
