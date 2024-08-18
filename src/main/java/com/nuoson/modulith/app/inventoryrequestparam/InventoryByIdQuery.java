package com.nuoson.modulith.app.inventoryrequestparam;

import com.nuoson.modulith.app.internal.BaseRequestParam;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 */
@Getter
@Setter
public class InventoryByIdQuery extends BaseRequestParam {

    @Schema(description = "库存产品id", requiredMode = RequiredMode.REQUIRED)
    @jakarta.validation.constraints.NotBlank(message = "商品编码（字段）不能为 BLANK")
    private String productId;

}
