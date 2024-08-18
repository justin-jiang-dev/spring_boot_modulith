package com.nuoson.modulith.app.internal;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BaseRequestParam {
    @Schema(description = "当前用户id, ** 由服务端提供 **", hidden = true, accessMode = AccessMode.READ_ONLY)
    @NotBlank(message = "当前用户id（字段）不能为 BLANK", groups = ExecutorArgsValidationGroup.class)
    private String currentUserId;
}
