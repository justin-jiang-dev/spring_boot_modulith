package com.nuoson.modulith.start.internal;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.nuoson.modulith.app.foundationmodel.BasicResultDTO;
import com.nuoson.modulith.domain.foundationmodel.ApiErrorCode.GeneralErrorCodeEnum;
import com.nuoson.modulith.domain.foundationmodel.BusinessException;
import com.nuoson.modulith.domain.foundationmodel.MsgCode;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // #region [** 异常分类捕获配置 ** ] -- ** Input Description **
    /**
     * 业务异常，返回 500
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = { BusinessException.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BasicResultDTO<Object> handleServiceException(BusinessException ex) {
        MsgCode msgCode = ex.getMsgCode();
        return BasicResultDTO.fail(msgCode.getCode(), ex.getMessage());
    }

    @ExceptionHandler(value = { ConstraintViolationException.class })
    /**
     * 处理约束违规异常的方法
     *
     * @param ex 约束违规异常对象
     * @return 包含错误信息的BasicResultDTO对象
     * @throws 无
     *
     * @ExceptionHandler 注解指定该方法用于处理 ConstraintViolationException 异常
     * @ResponseStatus 注解指定当发生异常时，HTTP响应状态码为 INTERNAL_SERVER_ERROR
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BasicResultDTO<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("ConstraintViolationException with error: {} and ex: ",
                buildConstraintViolationMessage((ConstraintViolationException) ex),
                ex);
        return BasicResultDTO.fail(GeneralErrorCodeEnum.CONSTRAINT_VIOLATION.getCode(),
                GeneralErrorCodeEnum.CONSTRAINT_VIOLATION.getMsg());
    }

    /**
     * 客户端传参类问题（返回 406） -- 参数格式不正确，例如 requestBody 不能为空 或 json 格式不正确等
     *
     * @param ex
     * @param request
     * @return
     */
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Object handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        log.warn("##MessageNotReadable Request={} with error: {}", request.getDescription(true), ex.getMessage());
        return BasicResultDTO.fail(String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()), "Request params error");
    }

    /**
     * 客户端传参类问题（返回 400） -- 参数校验异常，例如参数为空，参数格式不正确等
     * 
     * @param ex
     * @param request
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {
            BindException.class,
            // 用于捕获 RequestBody 参数校验异常
            MethodArgumentNotValidException.class,
            HandlerMethodValidationException.class
    })
    public Object handleValidationException(Exception ex, WebRequest request) {
        return buildApiResultWithBadRequest(ex);
    }

    /**
     * 请求方式不受支持，例如：GET请求，但是接口只支持POST请求
     *
     * @param ex
     * @param request
     * @return
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Object methodNotAllowed(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        log.warn("##methodNotAllowed Request={} with error: {}", request.getDescription(true), ex.getMessage());
        return BasicResultDTO.fail(String.valueOf(HttpStatus.METHOD_NOT_ALLOWED.value()),
                "Request method not supported");
    }

    /**
     * 未知异常（返回 503）
     * 
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Object handleException(Exception ex, WebRequest request) {

        String msg = "未知错误";
        log.warn("##Exception Request={} with error: ", request.getDescription(true), ex);
        return BasicResultDTO.fail(String.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()), msg);
    }
    // #endregion

    // #region [** 私有方法 ** ] -- ** Input Description **
    /**
     * 获取 api 返回结果，包含错误码（ 400 ）和错误信息
     * 
     * @param ex
     * @return
     */
    private BasicResultDTO<Object> buildApiResultWithBadRequest(Exception ex) {
        String msg = null;
        if (ex instanceof BindException) {
            List<ObjectError> allErrors = ((BindException) ex).getBindingResult().getAllErrors();
            StringBuilder errorMessageBuilder = new StringBuilder();
            allErrors.forEach(item -> {
                if (item instanceof FieldError) {
                    errorMessageBuilder
                            .append(String.format("'%s'%s", ((FieldError) item).getField(), item.getDefaultMessage()))
                            .append(";");
                } else {
                    errorMessageBuilder.append(item.toString()).append(";");
                }

            });
            msg = errorMessageBuilder.toString();
        } else if (ex instanceof ConstraintViolationException) {
            msg = buildConstraintViolationMessage((ConstraintViolationException) ex);
        } else {
            msg = ex.getMessage();
        }
        log.warn("handle exception:{} with msg: {}", ex.getClass().getName(), msg);

        return BasicResultDTO.fail(String.valueOf(HttpStatus.BAD_REQUEST.value()), msg);
    }

    /**
     * 构建约束违反信息的字符串表示
     *
     * @param ex 约束违反异常
     * @return 返回一个包含所有约束违反信息的字符串，每个约束信息之间用分号（;）分隔
     */
    private String buildConstraintViolationMessage(ConstraintViolationException ex) {
        StringBuilder errorMessageBuilder = new StringBuilder();
        ex.getConstraintViolations().forEach(
                constraintViolation -> errorMessageBuilder.append(constraintViolation.getMessage()).append(";"));
        return errorMessageBuilder.toString();
    }
    // #endregion

}
