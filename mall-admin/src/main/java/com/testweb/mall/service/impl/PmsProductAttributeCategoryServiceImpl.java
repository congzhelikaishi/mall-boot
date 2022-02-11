package com.testweb.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.testweb.mall.dao.PmsProductAttributeCategoryDao;
import com.testweb.mall.dto.PmsProductAttributeCategoryItem;
import com.testweb.mall.mapper.PmsProductAttributeCategoryMapper;
import com.testweb.mall.model.PmsProductAttributeCategory;
import com.testweb.mall.model.PmsProductAttributeCategoryExample;
import com.testweb.mall.service.PmsProductAttributeCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PmsProductAttributeCategoryServiceImpl implements PmsProductAttributeCategoryService {

    @Autowired
    private PmsProductAttributeCategoryMapper productAttributeCategoryMapper;

    @Autowired
    PmsProductAttributeCategoryDao productAttributeCategoryDao;

    /*
    创建属性分类
     */
    @Override
    public int create(String name) {
         PmsProductAttributeCategory productAttributeCategory = new PmsProductAttributeCategory();
         productAttributeCategory.setName(name);
         return productAttributeCategoryMapper.insertSelective(productAttributeCategory);
    }

    /*
    修改属性分类
     */
    @Override
    public int update(Long id, String name) {
        PmsProductAttributeCategory productAttributeCategory = new PmsProductAttributeCategory();
        productAttributeCategory.setName(name);
        productAttributeCategory.setId(id);
        return productAttributeCategoryMapper.updateByPrimaryKeySelective(productAttributeCategory);
    }

    /*
    删除属性分类
     */
    @Override
    public int delete(Long id) {
        return productAttributeCategoryMapper.deleteByPrimaryKey(id);
    }

    /*
    获取属性分类详情
     */
    @Override
    public PmsProductAttributeCategory getItem(Long id) {
        return productAttributeCategoryMapper.selectByPrimaryKey(id);
    }

    /*
     分页查询属性分类
      */
    @Override
    public List<PmsProductAttributeCategory> getList(Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return productAttributeCategoryMapper.selectByExample(new PmsProductAttributeCategoryExample());
    }

    /*
    获取包含属性的属性分类
     */
    @Override
    public List<PmsProductAttributeCategoryItem> getListWithAttr() {
        return productAttributeCategoryDao.getListWithAttr();
    }
}
