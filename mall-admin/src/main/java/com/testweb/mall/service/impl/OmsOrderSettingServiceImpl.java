package com.testweb.mall.service.impl;

import com.testweb.mall.mapper.OmsOrderSettingMapper;
import com.testweb.mall.model.OmsOrderSetting;
import com.testweb.mall.service.OmsOrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OmsOrderSettingServiceImpl implements OmsOrderSettingService {

    @Autowired
    private OmsOrderSettingMapper orderSettingMapper;

    /**
     * 获取指定订单设置
     */
    @Override
    public OmsOrderSetting getItem(Long id) {
        return orderSettingMapper.selectByPrimaryKey(id);
    }


    /**
     * 修改指定订单设置
     */
    @Override
    public int update(Long id, OmsOrderSetting orderSetting) {
        orderSetting.setId(id);
        return orderSettingMapper.updateByPrimaryKey(orderSetting);
    }
}
