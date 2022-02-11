package com.testweb.mall.bo;

import com.testweb.mall.model.UmsAdmin;
import com.testweb.mall.model.UmsResource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SpringSecurity需要的用户详情.
 */
public class AdminUserDetails implements UserDetails {

    private UmsAdmin umsAdmin;

    private List<UmsResource> resourceList;

    public AdminUserDetails(UmsAdmin umsAdmin,List<UmsResource> resourceList) {
        this.umsAdmin = umsAdmin;
        this.resourceList = resourceList;
    }
    // 获取用户权限，本质上是用户的角色信息
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // 返回的一个权限(主要还是包含了用户基本信息)
        //返回当前用户的角色
        return resourceList.stream() // 1.resourceList实体类集合 2.实体类集合.stream().map(需要for循环集合的方法)。collect(Collectors.toList())
                .map(role -> new SimpleGrantedAuthority(role.getId()+":"+role.getName()))  // map():用于映射每个元素到对应的结果  2.储存用户资源列表
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return umsAdmin.getPassword();
    }

    @Override
    public String getUsername() {
        return umsAdmin.getUsername();
    }

    // 账户是否过期
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 账户是否被锁定
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 密码是否过期
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 1.账户是否可用  2.判断账号状态0--》禁用， 1--》启用
    @Override
    public boolean isEnabled() {
        return umsAdmin.getStatus().equals(1);
    }
}
/*
UserDetailsService 可以知道最终交给Spring Security的是UserDetails 。
该接口是提供用户信息的核心接口。该接口实现仅仅存储用户的信息。
后续会将该接口提供的用户信息封装到认证对象Authentication中去。UserDetails 默认提供了：
用户的权限集， 默认需要添加ROLE_ 前缀
用户的加密后的密码， 不加密会使用{noop}前缀
应用内唯一的用户名
账户是否过期
账户是否锁定
凭证是否过期
用户是否可用
 */