package com.nuoson.modulith.app.foundationmodel;

import com.nuoson.modulith.domain.foundationmodel.ApiErrorCode;
import com.nuoson.modulith.domain.foundationmodel.MsgCode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "API 返回值包装类")
@Getter
@Setter
public class BasicResultDTO<T> {
    @Schema(description = "状态码")
    private String code;
    @Schema(description = "状态描述")
    private String msg;
    @Schema(description = "返回的数据")
    private T data;

    protected BasicResultDTO() {
    }

    public static <T> BasicResultDTO<T> success() {
        return success(null);
    }

    public static <T> BasicResultDTO<T> success(T data) {
        return success(data, null);
    }

    public static <T> BasicResultDTO<T> success(T data, String msg) {
        return build(ApiErrorCode.GeneralErrorCodeEnum.SUCCESS.getCode(), null, data);
    }

    public static <T> BasicResultDTO<T> fail(T data, String msg) {
        return build(ApiErrorCode.GeneralErrorCodeEnum.FAILED.getCode(), msg, data);
    }

    public static <T> BasicResultDTO<T> fail(T data) {
        return fail(data, null);
    }

    public static <T> BasicResultDTO<T> build(String code, String msg, T data) {
        BasicResultDTO<T> dto = new BasicResultDTO<>();
        dto.setCode(code);
        dto.setData(data);
        dto.setMsg(msg);
        return dto;
    }

    public static <T> BasicResultDTO<T> build(MsgCode msgCode, T data) {
        BasicResultDTO<T> dto = new BasicResultDTO<>();
        dto.setCode(msgCode.getCode());
        dto.setData(data);
        dto.setMsg(msgCode.getMsg());
        return dto;
    }

}
