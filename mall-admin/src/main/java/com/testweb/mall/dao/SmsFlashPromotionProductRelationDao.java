package com.testweb.mall.dao;

import com.testweb.mall.dto.SmsFlashPromotionProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 限时购商品关系管理自定义Dao
 */
@Repository
@Mapper
public interface SmsFlashPromotionProductRelationDao {
    /*
    获取限时购及相关商品信息
     */
    List<SmsFlashPromotionProduct> getList(@Param("flashPromotionId") Long flashPromotionId, @Param("flashPromotionSessionId") Long flashPromotionSessionId);
}
