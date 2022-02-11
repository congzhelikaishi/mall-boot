package com.testweb.mall.service;

import com.testweb.mall.model.SmsHomeBrand;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 首页品牌管理Service
 */
public interface SmsHomeBrandService {
    /*
    添加首页品牌推荐
     */
    @Transactional
    int create(List<SmsHomeBrand> homeBrandList);

    /*
    修改品牌推荐排序
     */
    int updateSort(Long id, Integer sort);

    /*
    批量删除商品推荐
     */
    int delete(List<Long> ids);

    /*
    批量更新推荐状态
     */
    int updateRecommendStatus(List<Long> ids, Integer recommendStatus);

    /*
    分页查询品牌排序
     */
    List<SmsHomeBrand> list(String brandName, Integer recommendStatus, Integer pageSize, Integer pageNum);
}
