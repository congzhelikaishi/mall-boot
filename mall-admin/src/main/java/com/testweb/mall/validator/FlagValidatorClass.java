package com.testweb.mall.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 状态约束校验器
 * 检验自义定注解上的数据是否和所需要的数据一致
 * 自义定注解的处理逻辑类
 * 使用校验时必须开启@Validated,否则不生效
 */
public class FlagValidatorClass implements ConstraintValidator<FlagValidator,Integer> { // 自定义校验器  FlagValidator自义定注解的名字

    // 是否强制校验
    private String[] values;

    //初始化传入的是注解
    @Override
    public void initialize(FlagValidator flagValidator) {
        this.values = flagValidator.value();
    }

    //进行校验的逻辑判断
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        boolean isValid = false;
        if(value==null){
            //当状态为空时使用默认值
            return true;
        }
        for (String s : values) {
            if (s.equals(String.valueOf(value))) {
                isValid = true;
                break;
            }
        }
        return isValid;
    }
}