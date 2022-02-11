package com.testweb.mall.service;

import com.testweb.mall.dto.PmsProductAttributeParam;
import com.testweb.mall.dto.ProductAttrInfo;
import com.testweb.mall.model.PmsProductAttribute;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品属性管理Service
 */
public interface PmsProductAttributeService {
    /*
    根据分类分页获取商品属性
     */
    List<PmsProductAttribute> getList(Long cid, Integer type, Integer pageSize, Integer pageNum);

    /*
    添加商品属性
     */
    @Transactional
    int create(PmsProductAttributeParam pmsProductAttributeParam);

    /*
    修改商品属性
     */
    int update(Long id, PmsProductAttributeParam productAttributeParam);

    /*
    获取单个商品属性信息
     */
    PmsProductAttribute getItem(Long id);

    /*
    批量删除商品属性
     */
    @Transactional
    int delete(List<Long> ids);

    /*
    获取商品分类对应属性列表
     */
    List<ProductAttrInfo> getProductAttrInfo(Long productCategoryId);
}
