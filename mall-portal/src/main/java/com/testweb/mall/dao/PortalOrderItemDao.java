package com.testweb.mall.dao;

import com.testweb.mall.model.OmsOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单商品信息管理自定义Dao
 */
@Repository
@Mapper
public interface PortalOrderItemDao {
    /*
    批量插入
     */
    int insertList(@Param("list")List<OmsOrderItem> list);
}
