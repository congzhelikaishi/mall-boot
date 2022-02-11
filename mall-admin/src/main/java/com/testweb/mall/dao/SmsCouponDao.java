package com.testweb.mall.dao;

import com.testweb.mall.dto.SmsCouponParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 优惠券管理自定义Dao
 */
@Repository
@Mapper
public interface SmsCouponDao {
    /**
     * 获取优惠券详情包括绑定关系
     */
    SmsCouponParam getItem(@Param("id") Long id);
}