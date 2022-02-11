package com.testweb.mall.dao;

import com.testweb.mall.domain.CartProduct;
import com.testweb.mall.domain.PromotionProduct;
import com.testweb.mall.model.SmsCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 前台购物车商品管理自定义Dao
 */
@Repository
@Mapper
public interface PortalProductDao {
    /*
    获取购物车商品信息
     */
    CartProduct getCartProduct(@Param("id") Long id);

    /*
    获取促销商品信息列表
     */
    List<PromotionProduct> getPromotionProductList(@Param("ids") List<Long> ids);

    /*
    获取可用优惠券列表
     */
    List<SmsCoupon> getAvailableCouponList(@Param("productId") Long productId, @Param("productCategoryId") Long productCategoryId);
}
