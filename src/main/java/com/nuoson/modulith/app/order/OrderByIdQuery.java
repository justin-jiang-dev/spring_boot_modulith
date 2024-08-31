package com.nuoson.modulith.app.order;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderByIdQuery {
    @Schema(description = "订单 id", requiredMode = RequiredMode.REQUIRED)
    @NotBlank(message = "订单 id 不能为 BLANK")
    private String orderId;
}
