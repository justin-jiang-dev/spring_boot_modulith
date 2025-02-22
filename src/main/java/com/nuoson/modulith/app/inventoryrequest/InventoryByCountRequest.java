package com.nuoson.modulith.app.inventoryrequest;

import com.nuoson.modulith.app.internal.BaseRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryByCountRequest extends BaseRequest {
    @Schema(description = "库存产品数量最少值", requiredMode = RequiredMode.REQUIRED)
    @Positive(message = "值必须是正整数，当前值为：${validatedValue}")
    private Long from;

    @Schema(description = "库存产品数量最大值", requiredMode = RequiredMode.REQUIRED)
    @Positive(message = "值必须是正整数，当前值为：${validatedValue}")
    private Long to;
}
