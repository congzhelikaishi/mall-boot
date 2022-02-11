package com.testweb.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.testweb.mall.mapper.SmsHomeNewProductMapper;
import com.testweb.mall.model.SmsHomeNewProduct;
import com.testweb.mall.model.SmsHomeNewProductExample;
import com.testweb.mall.service.SmsHomeNewProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 首页新品推荐管理Service实现类
 * Created by macro on 2018/11/6.
 */
@Service
public class SmsHomeNewProductServiceImpl implements SmsHomeNewProductService {
    @Autowired
    private SmsHomeNewProductMapper homeNewProductMapper;

    /*
    添加首页推荐
     */
    @Override
    public int create(List<SmsHomeNewProduct> homeNewProductList) {
        for (SmsHomeNewProduct SmsHomeNewProduct : homeNewProductList) {
            SmsHomeNewProduct.setRecommendStatus(1);
            SmsHomeNewProduct.setSort(0);
            homeNewProductMapper.insert(SmsHomeNewProduct);
        }
        return homeNewProductList.size();
    }

    /*
    修改推荐排序
     */
    @Override
    public int updateSort(Long id, Integer sort) {
        SmsHomeNewProduct homeNewProduct = new SmsHomeNewProduct();
        homeNewProduct.setId(id);
        homeNewProduct.setSort(sort);
        return homeNewProductMapper.updateByPrimaryKeySelective(homeNewProduct);
    }

    /*
    批量删除推荐
     */
    @Override
    public int delete(List<Long> ids) {
        SmsHomeNewProductExample example = new SmsHomeNewProductExample();
        example.createCriteria().andIdIn(ids);
        return homeNewProductMapper.deleteByExample(example);
    }

    /*
    批量更新排序状态
     */
    @Override
    public int updateRecommendStatus(List<Long> ids, Integer recommendStatus) {
        SmsHomeNewProductExample example = new SmsHomeNewProductExample();
        example.createCriteria().andIdIn(ids);
        SmsHomeNewProduct record = new SmsHomeNewProduct();
        record.setRecommendStatus(recommendStatus);
        return homeNewProductMapper.updateByExampleSelective(record,example);
    }

    /*
    分页查询推荐
     */
    @Override
    public List<SmsHomeNewProduct> list(String productName, Integer recommendStatus, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum,pageSize);
        SmsHomeNewProductExample example = new SmsHomeNewProductExample();
        SmsHomeNewProductExample.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(productName)){
            criteria.andProductNameLike("%"+productName+"%");
        }
        if(recommendStatus!=null){
            criteria.andRecommendStatusEqualTo(recommendStatus);
        }
        example.setOrderByClause("sort desc");
        return homeNewProductMapper.selectByExample(example);
    }
}