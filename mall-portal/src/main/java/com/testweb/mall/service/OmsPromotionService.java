package com.testweb.mall.service;

import com.testweb.mall.domain.CartPromotionItem;
import com.testweb.mall.model.OmsCartItem;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 促销管理Service
 */
public interface OmsPromotionService {
    /**
     * 计算购物车中的促销活动信息
     */
    List<CartPromotionItem> calcCartPromotion(List<OmsCartItem> cartItemList);
}
