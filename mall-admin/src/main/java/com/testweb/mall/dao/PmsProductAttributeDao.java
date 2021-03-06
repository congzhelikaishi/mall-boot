package com.testweb.mall.dao;

import com.testweb.mall.dto.ProductAttrInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品属性管理自定义Dao
 * Created by macro on 2018/5/23.
 */
@Repository
@Mapper
public interface PmsProductAttributeDao {
    /**
     * 获取商品属性信息
     */
    List<ProductAttrInfo> getProductAttrInfo(@Param("id") Long productCategoryId);
}
