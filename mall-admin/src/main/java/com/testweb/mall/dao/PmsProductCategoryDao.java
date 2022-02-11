package com.testweb.mall.dao;

import com.testweb.mall.dto.PmsProductCategoryWithChildrenItem;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品分类自定义Dao
 */
@Repository
@Mapper
public interface PmsProductCategoryDao {
    /**
     * 获取商品分类及其子分类
     */
    List<PmsProductCategoryWithChildrenItem> listWithChildren();
}
