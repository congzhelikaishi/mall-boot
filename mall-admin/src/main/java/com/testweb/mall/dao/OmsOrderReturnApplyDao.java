package com.testweb.mall.dao;

import com.testweb.mall.dto.OmsOrderReturnApplyResult;
import com.testweb.mall.dto.OmsReturnApplyQueryParam;
import com.testweb.mall.model.OmsOrderReturnApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单退货申请自定义Dao
 * Created by macro on 2018/10/18.
 */
@Repository
@Mapper
public interface OmsOrderReturnApplyDao {
    /**
     * 查询申请列表
     */
    List<OmsOrderReturnApply> getList(@Param("queryParam") OmsReturnApplyQueryParam queryParam);

    /**
     * 获取申请详情
     */
    OmsOrderReturnApplyResult getDetail(@Param("id")Long id);
}
