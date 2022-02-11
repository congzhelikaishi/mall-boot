package com.testweb.mall.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.testweb.mall.service.RedisService;
import com.testweb.mall.dao.UmsAdminRoleRelationDao;
import com.testweb.mall.mapper.UmsAdminRoleRelationMapper;
import com.testweb.mall.model.UmsAdmin;
import com.testweb.mall.model.UmsAdminRoleRelation;
import com.testweb.mall.model.UmsAdminRoleRelationExample;
import com.testweb.mall.model.UmsResource;
import com.testweb.mall.service.UmsAdminCacheService;
import com.testweb.mall.service.UmsAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UmsAdminCacheServiceImpl implements UmsAdminCacheService {
    @Autowired
    private UmsAdminService adminService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UmsAdminRoleRelationMapper adminRoleRelationMapper;
    @Autowired
    private UmsAdminRoleRelationDao adminRoleRelationDao;

    @Value("${redis.database}")
    private String REDIS_DATABASE; // redis数据库名
    @Value("${redis.expire.common}")
    private Long REDIS_EXPIRE; // redis设置缓存时间
    @Value("${redis.key.admin}")
    private String REDIS_KEY_ADMIN; // redis缓存用户信息
    @Value("${redis.key.resourceList}")
    private String REDIS_KEY_RESOURCE_LIST; // redis缓存用户资源列表

    /*
    删除后台用户缓存
     */
    @Override
    public void delAdmin(Long adminId) {
        UmsAdmin admin = adminService.getItem(adminId);  // 根据id获取用户名
        if (admin != null){
            String key = REDIS_DATABASE + ":" + REDIS_KEY_ADMIN + ":" + adminId;
            redisService.del(key);
        }
    }

    /*
    删除后台用户资源列表缓存
     */
    @Override
    public void delResourceList(Long adminId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_RESOURCE_LIST + ":" + adminId;
        redisService.del(key);
    }

    /*
    当角色相关资源信息改变时删除后台相关缓存
     */
    @Override
    public void delResourceListByRole(Long roleId) {
        UmsAdminRoleRelationExample example = new UmsAdminRoleRelationExample();
        example.createCriteria().andAdminIdEqualTo(roleId);
        relationList(example);
    }

    /*
    当资源信息改变时，删除资源项目后台用户缓存
     */
    @Override
    public void delResourceListByRoleIds(List<Long> roleIds) {
        UmsAdminRoleRelationExample example = new UmsAdminRoleRelationExample();
        example.createCriteria().andAdminIdIn(roleIds);
        relationList(example);
    }

    /*
    delResourceListByRole和delResourceListByRoles重复使用的代码
     */
    private void relationList(UmsAdminRoleRelationExample example) {
        List<UmsAdminRoleRelation> relationList = adminRoleRelationMapper.selectByExample(example);
        if (CollUtil.isNotEmpty(relationList)){ // 判断用户关系列表是否不为空
            String keyPrefix = REDIS_DATABASE + ":" + REDIS_KEY_RESOURCE_LIST + ":";
            List<String> keys = relationList.stream().map(relation -> keyPrefix + relation.getAdminId()).collect(Collectors.toList());
            redisService.del(keys);
        }
    }

    /*
    当资源信息改变时，删除资源项目后台用户缓存
     */
    @Override
    public void delResourceListByResource(Long resourceId) {
        List<Long> adminIdList = adminRoleRelationDao.getAdminIdList(resourceId); // 获取资源相关用户ID列表
        if (CollUtil.isNotEmpty(adminIdList)){
            String keyPrefix = REDIS_DATABASE + ":" + REDIS_KEY_RESOURCE_LIST + ":";
            List<String> keys = adminIdList.stream().map(admin -> keyPrefix + admin).collect(Collectors.toList()); // 1.steam():把一个源数据，可以是集合，数组，I/O channel， 产生器generator 等，转化成流 2.map():用于映射每个元素到对应的结果 3.Collectors(): 类实现了很多归约操作，例如将流转换成集合和聚合元素。Collectors 可用于返回列表或字符串
            /*
            stream()优点
            无存储。stream不是一种数据结构，它只是某种数据源的一个视图，数据源可以是一个数组，Java容器或I/O channel等。
            为函数式编程而生。对stream的任何修改都不会修改背后的数据源，比如对stream执行过滤操作并不会删除被过滤的元素，而是会产生一个不包含被过滤元素的新stream。
            惰式执行。stream上的操作并不会立即执行，只有等到用户真正需要结果的时候才会执行。
            可消费性。stream只能被“消费”一次，一旦遍历过就会失效，就像容器的迭代器那样，想要再次遍历必须重新生成
             */
            redisService.del(keys);
        }
    }

    /*
    获取后台缓存用户信息
     */
    @Override
    public UmsAdmin getAdmin(String username) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_ADMIN + ":" + username;
        return (UmsAdmin) redisService.get(key);
    }

    /*
     设置缓存后台用户信息
     */
    @Override
    public void setAdmin(UmsAdmin admin) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_ADMIN + ":" + admin.getUsername();
        redisService.set(key, admin, REDIS_EXPIRE);
    }

    /*
    获取缓存后台用户资源列表
     */
    @Override
    public List<UmsResource> getResourceList(Long adminId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_RESOURCE_LIST + ":" + adminId;
        return (List<UmsResource>) redisService.get(key);
    }

    /*
    设置缓存后台用户资源列表
     */
    @Override
    public void setResourceList(Long adminId, List<UmsResource> resourceList) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_RESOURCE_LIST + ":" + adminId;
        redisService.set(key, resourceList, REDIS_EXPIRE);
    }
}
