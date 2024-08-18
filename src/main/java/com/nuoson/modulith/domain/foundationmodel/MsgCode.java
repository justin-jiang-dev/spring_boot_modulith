package com.nuoson.modulith.domain.foundationmodel;

import com.nuoson.modulith.domain.internal.exception.Code;
import com.nuoson.modulith.domain.internal.exception.CodeRepository;

/**
 * 获取错误码和信息
 **/
public interface MsgCode {

    /**
     * 获取消息
     *
     * @return msg
     */
    default String getMsg() {
        Code code = CodeRepository.get(this);
        return String.format("%s（%s）", code.getMsg(), code.getCode());
    }

    /**
     * 获取code
     *
     * @return code
     */
    default String getCode() {
        return CodeRepository.get(this).getCode();
    }
}
