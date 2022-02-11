package com.testweb.mall.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Swagger自定义配置
 */
@Data//注解在类上，相当于同时使用了@ToString、@EqualsAndHashCode、@Getter、@Setter和@RequiredArgsConstrutor这些注解
@EqualsAndHashCode(callSuper = false)// 注解的作用就是自动的给model bean实现equals方法和hashcode方法
@Builder
/*
@Builder作用：
创建一个名为ThisClassBuilder的内部静态类，并具有和实体类形同的属性（称为构建器）。
在构建器中：对于目标类中的所有的属性和未初始化的final字段，都会在构建器中创建对应属性。
在构建器中：创建一个无参的default构造函数。
在构建器中：对于实体类中的每个参数，都会对应创建类似于setter的方法，只不过方法名与该参数名相同。 并且返回值是构建器本身（便于链式调用），如上例所示。
在构建器中：一个build()方法，调用此方法，就会根据设置的值进行创建实体对象。
在构建器中：同时也会生成一个toString()方法。
在实体类中：会创建一个builder()方法，它的目的是用来创建构建器。
 */
public class SwaggerProperties {
    /**
     * API文档生成基础路径
     */
    private String apiBasePackage;
    /**
     * 是否要启用登录认证
     */
    private boolean enableSecurity;
    /**
     * 文档标题
     */
    private String title;
    /**
     * 文档描述
     */
    private String description;
    /**
     * 文档版本
     */
    private String version;
    /**
     * 文档联系人姓名
     */
    private String contactName;
    /**
     * 文档联系人网址
     */
    private String contactUrl;
    /**
     * 文档联系人邮箱
     */
    private String contactEmail;
}
