package com.nuoson.modulith.domain.foundationmodel;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import com.nuoson.modulith.domain.internal.exception.C;
import com.nuoson.modulith.domain.internal.exception.CodePrefix;

/**
 * 系统错误码集合
 * 
 * 错误码编码采用 YYYZZZ 的模式，Y是服务或模块标志，代表哪个服务或模块；Z是错误编号
 * 
 * 例如 -- DIC001 - DIC（dict）代表词典服务错误码
 */

public interface ApiErrorCode {
    /**
     * 通用错误码，不需要前缀（YYY）
     */
    @CodePrefix("")
    enum GeneralErrorCodeEnum implements MsgCode {
        /** */
        @C(value = "0", msg = "成功")
        SUCCESS,
        /** */
        @C(value = "1", msg = "操作失败")
        FAILED,
        /** */
        @C(value = "401", msg = "未授权")
        UNAUTHORIZED,
        /** */
        @C(value = "403", msg = "无权限")
        FORBIDDEN,
        /** */
        @C(value = "404", msg = "接口不存在")
        NOT_FOUND,
        /** */
        @C(value = "405", msg = "功能未实现")
        NOT_IMPLEMENTED,

        @C(value = "1000", msg = "依赖服务数据验证异常")
        CONSTRAINT_VIOLATION,
    }

    /**
     * Inventory 模块错误码
     */
    @CodePrefix("INV")
    enum InventoryErrorCodeEnum implements MsgCode {

        /** */
        @C(value = "001", msg = "未知错误")
        UNKNOWN,

    }

    /**
     * ORDER 模块错误码
     */
    @CodePrefix("ORD")
    enum OrderErrorCodeEnum implements MsgCode {

        /** */
        @C(value = "001", msg = "未知错误")
        UNKNOWN,
        /** */
        @C(value = "002", msg = "产品不存在")
        PRODUCT_MISSING,
        /** */
        @C(value = "003", msg = "产品数量不足")
        PRODUCT_COUNT_NOT_ENOUGH,
        /** */
        @C(value = "004", msg = "数据库更新(CAS)失败")
        DB_CAS_UPDATE_FAILED,
        /** */
        @C(value = "005", msg = "数据库更新失败")
        DB_UPDATE_FAILED,

    }

    /**
     * 返回上述所有的错误码，用于 Swagger 生成错误码文档
     * 
     * @return
     */
    static Map<String, String> toErrorCodes() {
        Map<String, String> errorCodesMap = new TreeMap<>();
        Stream.of(GeneralErrorCodeEnum.values()).forEach(item -> errorCodesMap.put(item.getCode(), item.getMsg()));
        Stream.of(InventoryErrorCodeEnum.values()).forEach(item -> errorCodesMap.put(item.getCode(), item.getMsg()));

        return errorCodesMap;
    }

}
