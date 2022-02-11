package com.testweb.mall.api;

/*
通用操作返回对象
 */
public class CommonResult<T> {
    /*
    状态码
     */
    private long code;
    /*
    提示信息
     */
    private String message;
    /*
    封装数据
    需要发送返回前端的数据（data）
     */
    private T data; // 集合泛型

    protected CommonResult(){}

    protected CommonResult(long code, String message, T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /*
    成功返回结果
    data:获取的数据
     */
    public static <T> CommonResult<T> success(T data){
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /*
    成功返回结果
    data:获取的数据
    message:提示信息
     */
    public static <T> CommonResult<T> success(T data, String message){
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /*
    返回失败结果
    errorCode:错误码
     */
    public static <T> CommonResult<T> failed(IErrorCode errorCode){ // 前端返回码，前端返回信息
        return new CommonResult<T>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /*
    失败返回结果
    errorCode:错误码
    message:错误信息
     */
    public static <T> CommonResult<T> failed(IErrorCode errorCode, String message){ // 前端返回状态码，自己设置返回信息
        return new CommonResult<T>(errorCode.getCode(), message, null);
    }

    /*
    失败返回结果
    message:提示信息
     */
    public static <T> CommonResult<T> failed(String message){ //前端返回信息，返回自己设置的状态码
        return new CommonResult<T>(ResultCode.FAILED.getCode(), message, null);
    }

    /*
    失败返回结果
     */
    public static <T> CommonResult<T> failed(){ // 返回自己设置的操作错误信息及状态码
        return failed(ResultCode.FAILED);
    }

    /*
    参数验证失败返回结果
     */
    public static <T> CommonResult<T> validateFailed(){
        return failed(ResultCode.VALIDATE_FAILED);
    }

    /*
    参数验证失败返回结果
    message:提示信息
     */
    public static <T> CommonResult<T> validateFailed(String message){
        return new CommonResult<T>(ResultCode.VALIDATE_FAILED.getCode(), message, null);
    }
    /*
    未登录返回结果
     */
    public static <T> CommonResult<T> unauthorized(T data){ // 返回自己设置的数据
        return new CommonResult<T>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(), data);
    }

    /*
    未授权返回结果
     */
    public static <T> CommonResult<T> forbidden(T data){
        return new CommonResult<T>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage(), data);
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
