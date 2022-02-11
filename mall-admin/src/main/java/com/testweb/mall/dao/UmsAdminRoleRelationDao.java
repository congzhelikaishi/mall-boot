package com.testweb.mall.dao;

import com.testweb.mall.model.UmsAdminRoleRelation;
import com.testweb.mall.model.UmsResource;
import com.testweb.mall.model.UmsRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
后台用户与角色关系管理自定义Dao
 */

@Repository
@Mapper
public interface UmsAdminRoleRelationDao {
    /*
    批量插入用户角色关系
     */
    int insertList(@Param("list") List<UmsAdminRoleRelation> adminRoleRelations);

    /*
    获取用户所有角色
     */
    List<UmsRole> getRoleList(@Param("adminId") Long adminId);

    /*
    获取用户所有可访问资源
     */
    List<UmsResource> getResourceList(@Param("adminId") Long adminId);

    /*
    获取资源相关用户ID列表
     */
    List<Long> getAdminIdList(@Param("resourceId") Long resourceId);
}
