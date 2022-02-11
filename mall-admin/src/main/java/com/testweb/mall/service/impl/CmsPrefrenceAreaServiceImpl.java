package com.testweb.mall.service.impl;

import com.testweb.mall.mapper.CmsPrefrenceAreaMapper;
import com.testweb.mall.model.CmsPrefrenceArea;
import com.testweb.mall.model.CmsPrefrenceAreaExample;
import com.testweb.mall.service.CmsPrefrenceAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * 商品优选管理Service实现类
 */
@Service
public class CmsPrefrenceAreaServiceImpl implements CmsPrefrenceAreaService {
    @Autowired
    private CmsPrefrenceAreaMapper prefrenceAreaMapper;

    /*
    获取所有优选专区
     */
    @Override
    public List<CmsPrefrenceArea> listAll() {
        return prefrenceAreaMapper.selectByExample(new CmsPrefrenceAreaExample());
    }
}
