package com.testweb.mall.exception;

import com.testweb.mall.api.CommonResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
/*
@ControllerAdvice:
全局异常处理
全局数据绑定
全局数据预处理
 */
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ApiException.class)//捕获 全部异常
    public CommonResult handle(ApiException e) {
        if (e.getErrorCode() != null) {
            return CommonResult.failed(e.getErrorCode());
        }
        return CommonResult.failed(e.getMessage());
    }

    /*
    对方法参数校验异常处理方法@Validated异常的处理
    处理请求参数格式错误 @RequestBody上validate失败后抛出的异常是MethodArgumentNotValidException异常。
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public CommonResult handleValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult(); // 获取参数验证异常抛出的错误
        String message = null;
        if (bindingResult.hasErrors()) { // 判断如果这个结果有错误则
            FieldError fieldError = bindingResult.getFieldError(); // 1.获取拦截的错误 2.通过该方法返回全部错误信息的集合List， 验证器会将每条错误信息都封装一个FieldError对象中，这里面有包含错误的字段和错提示的message信息
            if (fieldError != null) {
                message = fieldError.getField()+fieldError.getDefaultMessage(); // 1.FieldError的 getField():获取出现错误的字段    2.FieldError的 getDefaultMessage():获取错误提示信息
            }
        }
        return CommonResult.validateFailed(message);
    }

    /*
    对方法参数校验异常处理方法对@Validated异常的处理
     */
    @ExceptionHandler(value = BindException.class)
    public CommonResult handleValidException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField()+fieldError.getDefaultMessage();
            }
        }
        return CommonResult.validateFailed(message);
    }
}
