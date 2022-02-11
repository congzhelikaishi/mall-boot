package com.testweb.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.testweb.mall.dto.PmsBrandParam;
import com.testweb.mall.mapper.PmsBrandMapper;
import com.testweb.mall.mapper.PmsProductMapper;
import com.testweb.mall.model.PmsBrand;
import com.testweb.mall.model.PmsBrandExample;
import com.testweb.mall.model.PmsProduct;
import com.testweb.mall.model.PmsProductExample;
import com.testweb.mall.service.PmsBrandService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
@Service
public class PmsBrandServiceImpl implements PmsBrandService {

    @Autowired
    private PmsBrandMapper brandMapper;

    @Autowired
    private PmsProductMapper productMapper;

    /*
    获取所有品牌
     */
    @Override
    public List<PmsBrand> listAllBrand() {
        return brandMapper.selectByExample(new PmsBrandExample());
    }

    /*
    创建品牌
     */
    @Override
    public int createBrand(PmsBrandParam pmsBrandParam) {
        PmsBrand pmsBrand = new PmsBrand();
        BeanUtils.copyProperties(pmsBrandParam, pmsBrand);
        /*
        BeanUtils.copyProperties("转换前的类", "转换后的类");
        BeanUtils.copyProperties(a, b);
            b中的存在的属性，a中一定要有，但是a中可以有多余的属性；
            a中与b中相同的属性都会被替换，不管是否有值；
            a、 b中的属性要名字相同，才能被赋值，不然的话需要手动赋值；
            Spring的BeanUtils的CopyProperties方法需要对应的属性有getter和setter方法；
            如果存在属性完全相同的内部类，但是不是同一个内部类，即分别属于各自的内部类，则spring会认为属性不同，不会copy；
            spring和apache的copy属性的方法源和目的参数的位置正好相反，所以导包和调用的时候都要注意一下。
         */
        // 如果创建时首字母为空，取名称的第一个为首字母
        if (StringUtils.isEmpty(pmsBrand.getFirstLetter())){
            pmsBrand.setFirstLetter(pmsBrand.getName().substring(0, 1));  // substring获取索引指定字段
        }
        return brandMapper.insertSelective(pmsBrand);
    }

    /*
    修改品牌
     */
    @Override
    public int updateBrand(Long id, PmsBrandParam pmsBrandParam) {
        PmsBrand pmsBrand = new PmsBrand();
        BeanUtils.copyProperties(pmsBrandParam, pmsBrand);
        pmsBrand.setId(id);
        // 如果创建时首字母为空，取名称的第一个为首字母
        if (StringUtils.isEmpty(pmsBrand.getFirstLetter())){
            pmsBrand.setFirstLetter(pmsBrand.getName().substring(0, 1));
        }
        // 更新品牌时要更新商品中的品牌名称
        PmsProduct product = new PmsProduct();
        product.setBrandName(pmsBrand.getName());
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andBrandIdEqualTo(id);
        productMapper.updateByExampleSelective(product, example);
        /*
        updateByExampleSelective是将一行中某几个属性更新,而不改变其他的值
        参数1:这个参数是让传入一个对象,就是你要修改的那条数据所对应的对象
        参数2:传入xxxExample就可以
         */
        return brandMapper.updateByPrimaryKeySelective(pmsBrand);
        // 接收的参数为对应于数据库的实体类对象，利用字段的自动匹配进行更新表的操作，如果传入obj对象中的某个属性值为null，则不进行数据库对应字段的更新
    }

    /*
    删除品牌
     */
    @Override
    public int deleteBrand(Long id) {
        return brandMapper.deleteByPrimaryKey(id);
    }

    /*
    批量删除品牌
     */
    @Override
    public int deleteBrand(List<Long> ids) {
        PmsBrandExample pmsBrandExample = new PmsBrandExample();
        pmsBrandExample.createCriteria().andIdIn(ids);
        return brandMapper.deleteByExample(pmsBrandExample);
    }

    /*
    分页查询品牌
     */
    @Override
    public List<PmsBrand> listBrand(String keyword, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PmsBrandExample pmsBrandExample = new PmsBrandExample();
        pmsBrandExample.setOrderByClause("sort desc");// 按照sort条件降序DESC排序
        PmsBrandExample.Criteria criteria = pmsBrandExample.createCriteria();
        if (!StringUtils.isEmpty(keyword)){
            criteria.andNameLike("%" + keyword + "%");
        }
        return brandMapper.selectByExample(pmsBrandExample); // selectByExample传入整个实体类对象
    }

    /*
    获取品牌详情
     */
    @Override
    public PmsBrand getBrand(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    /*
    修改显示状态
     */
    @Override
    public int updateShowStatus(List<Long> ids, Integer showStatus) {
        PmsBrand pmsBrand = new PmsBrand();
        pmsBrand.setShowStatus(showStatus);
        PmsBrandExample pmsBrandExample = new PmsBrandExample();
        pmsBrandExample.createCriteria().andIdIn(ids);
        return brandMapper.updateByExampleSelective(pmsBrand, pmsBrandExample);
    }

    /*
    修改厂家制造商状态
     */
    @Override
    public int updateFactoryStatus(List<Long> ids, Integer factoryStatus) {
       PmsBrand pmsBrand = new PmsBrand();
       pmsBrand.setFactoryStatus(factoryStatus);
       PmsBrandExample pmsBrandExample = new PmsBrandExample();
       pmsBrandExample.createCriteria().andIdIn(ids);
       return brandMapper.updateByExampleSelective(pmsBrand, pmsBrandExample);
    }
}
