package com.testweb.mall.service;

import com.testweb.mall.model.SmsHomeRecommendProduct;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 首页人气推荐管理
 */
public interface SmsHomeRecommendProductService {
    /*
    添加首页推荐
     */
    @Transactional
    int create(List<SmsHomeRecommendProduct> homeRecommendProductList);

    /*
    修改推荐排序
     */
    int updateSort(Long id, Integer sort);

    /*
    批量删除推荐
     */
    int delete(List<Long> ids);

    /*
    批量更新推荐状态
     */
    int updateRecommendStatus(List<Long> ids, Integer recommendStatus);

    /*
    分页查询推荐
     */
    List<SmsHomeRecommendProduct> list(String product, Integer recommendStatus, Integer pageSize, Integer pageNum);
}
