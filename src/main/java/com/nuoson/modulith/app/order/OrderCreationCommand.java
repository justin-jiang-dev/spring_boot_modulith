package com.nuoson.modulith.app.order;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderCreationCommand {
    @Schema(description = "产品id", requiredMode = RequiredMode.REQUIRED)
    @NotBlank(message = "商品编码（字段）不能为 BLANK")
    private String productId;

    @Schema(description = "产品个数", requiredMode = RequiredMode.REQUIRED)
    @Min(value = 1, message = "产品个数不能小于 ${validatedValue}")
    private Integer count;
}
