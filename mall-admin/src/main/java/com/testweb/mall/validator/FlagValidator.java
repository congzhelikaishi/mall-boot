package com.testweb.mall.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 用于验证状态是否在指定范围内的注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)  // 注解有效时间，程序运行时有效
@Target({ElementType.FIELD,ElementType.PARAMETER}) // 用于描述注解的使用范围（即：被描述的注解可以用在什么地方）
@Constraint(validatedBy = FlagValidatorClass.class)  // 代表注解的处理逻辑是那个
public @interface FlagValidator { // 自义定注解

    //是否强制校验
    String[] value() default {};

    // 校验不通过时的报错信息
    String message() default "flag is not found";

    // 将validator进行分类，不同的类group中会执行不同的validator操作
    Class<?>[] groups() default {};

    // 主要是针对bean，很少使用
    Class<? extends Payload>[] payload() default {};
}
