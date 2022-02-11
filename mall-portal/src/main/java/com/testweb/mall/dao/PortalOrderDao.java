package com.testweb.mall.dao;

import com.testweb.mall.domain.OmsOrderDetail;
import com.testweb.mall.model.OmsOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 前台订单管理Dao
 */
@Repository
@Mapper
public interface PortalOrderDao {
    /*
    获取订单及下单商品详情
     */
    OmsOrderDetail getDetail(@Param("orderId") Long orderId);

    /*
    修改pms_sku_stock表的锁定库存及真实库存
     */
    int updateSkuStock(@Param("itemList")List<OmsOrderItem> OrderItemList);

    /*
    获取超时订单
     */
    List<OmsOrderDetail> getTimeOutOrders(@Param("minute") Integer minute);

    /*
    批量修改订单状态
     */
    int updateOrderStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /*
    解除取消订单的库存锁定
     */
    int releaseSkuStockLock(@Param("itemList") List<OmsOrderItem> orderItemList);
}
