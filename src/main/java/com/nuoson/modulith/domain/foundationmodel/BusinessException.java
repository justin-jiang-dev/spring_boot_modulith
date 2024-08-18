package com.nuoson.modulith.domain.foundationmodel;

import java.text.MessageFormat;

/**
 * 自定义异常，最终由全局异常统一处理
 */
public class BusinessException extends RuntimeException {
    // region[Variables] -- ** ORDER BY NAME **

    private static final long serialVersionUID = -1L;

    private final transient MsgCode msgCode;

    // region

    // region[Properties] -- ** ORDER BY NAME **

    public MsgCode getMsgCode() {
        return msgCode;
    }
    // region

    // region[Constructor]

    public BusinessException(String msg) {
        super(msg);
        this.msgCode = ApiErrorCode.GeneralErrorCodeEnum.FAILED;
    }

    public BusinessException(String msg, Throwable throwable) {
        super(msg, throwable);
        this.msgCode = ApiErrorCode.GeneralErrorCodeEnum.FAILED;
    }

    public BusinessException(MsgCode msgCode) {
        super(msgCode.getMsg());
        this.msgCode = msgCode;
    }

    public BusinessException(MsgCode msgCode, Object... args) {
        super(new MessageFormat(msgCode.getMsg()).format(args));
        this.msgCode = msgCode;
    }
    // region

    // region[Methods]

    // region

    // region[Helps]

    // region

}
