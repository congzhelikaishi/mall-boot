package com.testweb.mall.dao;

import com.testweb.mall.dto.PmsProductAttributeCategoryItem;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品属性分类管理自定义Dao
 * Created by macro on 2018/5/24.
 */
@Repository
@Mapper
public interface PmsProductAttributeCategoryDao {
    /**
     * 获取包含属性的商品属性分类
     */
    List<PmsProductAttributeCategoryItem> getListWithAttr();
}
