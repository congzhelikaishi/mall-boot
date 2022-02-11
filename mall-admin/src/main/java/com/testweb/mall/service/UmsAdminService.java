package com.testweb.mall.service;

import com.testweb.mall.dto.UmsAdminParam;
import com.testweb.mall.dto.UpdateAdminPasswordParam;
import com.testweb.mall.model.UmsAdmin;
import com.testweb.mall.model.UmsResource;
import com.testweb.mall.model.UmsRole;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
后台用户管理Service
 */

public interface UmsAdminService {
    /*
    根据用户名获取后台管理员
     */
    UmsAdmin getAdminByUsername(String username);
    /*
    注册功能
     */
    UmsAdmin register(UmsAdminParam umsAdminParam);
    /**
     * 登录功能
     */
    String login(String username, String password);

    /*
    刷新token的功能
     */
    String refreshToken(String oldToken);

    /*
    根据id获取用户
     */
    UmsAdmin getItem(Long id);

    /*
    根据用户名或昵称分页查询用户
     */
    List<UmsAdmin> list(String keyword, Integer pageSize, Integer pageNum);

    /*
    修改指定用户信息
     */
    int update(Long id, UmsAdmin admin);

    /*
    删除指定用户信息
     */
    int delete(Long id);

    /*
    修改用户角色关系
     */
    @Transactional // 开启事务
    int updateRole(Long adminId, List<Long> roleIds);

    /*
    获取用户对应角色
     */
    List<UmsRole> getRoleList(Long adminId);

    /*
    获取指定用户的可访问资源
     */
    List<UmsResource> getResourceList(Long adminId);

    /*
    修改密码
     */
    int updatePassword(UpdateAdminPasswordParam updateAdminPasswordParam);

    /*
    获取用户信息
     */
    UserDetails loadUserByUsername(String username);

}
