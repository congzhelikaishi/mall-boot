package com.testweb.mall.dao;

import com.testweb.mall.domain.SmsCouponHistoryDetail;
import com.testweb.mall.model.SmsCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 会员优惠劵领取记录管理自定义Dao
 */
@Repository
@Mapper
public interface SmsCouponHistoryDao {
    /*
    获取优惠券历史详情
     */
    List<SmsCouponHistoryDetail> getDetailList(@Param("memberId") Long memberId);

    /*
    获取指定优惠劵列表
     */
    List<SmsCoupon> getCouponList(@Param("memberId") Long memberId, @Param("useStatus") Integer useStatus);
}
