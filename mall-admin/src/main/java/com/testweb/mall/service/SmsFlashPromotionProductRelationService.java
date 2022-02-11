package com.testweb.mall.service;

import com.testweb.mall.dto.SmsFlashPromotionProduct;
import com.testweb.mall.model.SmsFlashPromotionProductRelation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 限时购商品关联管理Service
 */
public interface SmsFlashPromotionProductRelationService {
    /*
    批量添加关联
     */
    @Transactional
    int create(List<SmsFlashPromotionProductRelation> relationList);

    /*
    修改关联信息
     */
    int update(Long id, SmsFlashPromotionProductRelation relation);

    /*
    删除关联
     */
    int delete(Long id);

    /*
    获取关联详情
     */
    SmsFlashPromotionProductRelation getItem(Long id);

    /*
    分页查询相关商品及促销信息
     */
    List<SmsFlashPromotionProduct> list(Long flashPromotionId, Long flashPromotionSessionId, Integer pageSize, Integer pageNum);

    /*
    根据活动和场次id获取商品关系数量
     */
    long getCount(Long flashPromotionId, Long flashPromotionSessionId);
}
