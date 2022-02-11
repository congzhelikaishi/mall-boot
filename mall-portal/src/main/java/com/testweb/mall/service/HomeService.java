package com.testweb.mall.service;

import com.testweb.mall.domain.HomeContentResult;
import com.testweb.mall.model.CmsSubject;
import com.testweb.mall.model.PmsProduct;
import com.testweb.mall.model.PmsProductCategory;

import java.util.List;

/**
 * 首页内容管理Service
 */
public interface HomeService {
    /*
    获取首页内容
     */
    HomeContentResult content();

    /*
    首页商品推荐
     */
    List<PmsProduct> recommendProductList(Integer pageSize, Integer pageNum);

    /*
    获取商品分类
     */
    List<PmsProductCategory> getProductCateList(Long parentId);

    /*
    根据专题分类分页获取专题
     */
    List<CmsSubject> getSubjectList(Long cateId, Integer pageSize, Integer pageNum);

    /*
    分页获取人气推荐商品
     */
    List<PmsProduct> hotProductList(Integer pageNum, Integer pageSize);

    /*
    分页获取新品推荐商品
     */
    List<PmsProduct> newProductList(Integer pageNum, Integer pageSize);
}
