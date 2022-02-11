package com.testweb.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/*
用户登录参数
 */
@Getter
@Setter
public class UmsAdminParam {
    @NotEmpty
    @ApiModelProperty(value = "用户名", required = true) // @ApiModelProperty是swagger的注解，它的作用是添加和操作属性模块的数据
    private String username;
    @NotEmpty
    @ApiModelProperty(value = "密码", required = true)
    private String password;
    @ApiModelProperty(value = "用户头像")
    private String icon;
    @Email
    private String email;
    @ApiModelProperty(value = "用户昵称")
    private String nicKName;
    @ApiModelProperty(value = "备注")
    private String note;
}
