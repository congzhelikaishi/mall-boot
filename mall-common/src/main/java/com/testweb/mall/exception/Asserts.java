package com.testweb.mall.exception;

import com.testweb.mall.api.IErrorCode;

/*
断言处理类，用于抛出各种API异常
无返回值，参数不同不用在意名字相同
类似于直接抛出指定异常
 */
public class Asserts { // 自己设置需要抛出的异常内容
    public static void fail(String message){
        throw new ApiException(message);
    }

    public static void fail(IErrorCode errorCode){
        throw new ApiException(errorCode);
    }
}
