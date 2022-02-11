package com.testweb.mall.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Controller层的日志封装类
 * 统一返回值格式
 */
@Data//注解在类上，相当于同时使用了@ToString、@EqualsAndHashCode、@Getter、@Setter和@RequiredArgsConstrutor这些注解
@EqualsAndHashCode(callSuper = false)//注解的作用就是自动的给model bean实现equals方法和hashcode方法，在这里不调用父类的属性（false）
public class WebLog {
    /*
    操作描述
     */
    private String description;

    /*
    操作用户
     */
    private String username;

    /*
    操作时间
     */
    private Long startTime;

    /*
    消耗时间
     */
    private Integer spendTime;

    /*
    根路径
     */
    private String basePath;

    /*
    URI
     */
    private String uri;

    /*
    URL
     */
    private String url;

    /*
    请求类型
     */
    private String method;

    /*
    IP地址
     */
    private String ip;

    /*
    请求参数
     */
    private Object parameter;

    /*
    返回结果
     */
    private Object result;
}
