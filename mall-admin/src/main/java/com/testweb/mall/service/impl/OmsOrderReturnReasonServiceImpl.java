package com.testweb.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.testweb.mall.mapper.OmsOrderReturnReasonMapper;
import com.testweb.mall.model.OmsOrderReturnReason;
import com.testweb.mall.model.OmsOrderReturnReasonExample;
import com.testweb.mall.service.OmsOrderReturnReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 订单原因管理Service
 */
@Service
public class OmsOrderReturnReasonServiceImpl implements OmsOrderReturnReasonService {

    @Autowired
    private OmsOrderReturnReasonMapper returnReasonMapper;

    /*
    添加退货原因
     */
    @Override
    public int create(OmsOrderReturnReason returnReason) {
        returnReason.setCreateTime(new Date());
        return returnReasonMapper.insert(returnReason);
    }

    /*
    修改退货原因
     */
    @Override
    public int update(Long id, OmsOrderReturnReason returnReason) {
        returnReason.setId(id);
        return returnReasonMapper.updateByPrimaryKey(returnReason);
    }

    /*
    批量删除退货原因
     */
    @Override
    public int delete(List<Long> ids) {
        OmsOrderReturnReasonExample example = new OmsOrderReturnReasonExample();
        example.createCriteria().andIdIn(ids); // 批量处理放入集合
        return returnReasonMapper.deleteByExample(example);
    }

    /*
    分页获取退货原因
     */
    @Override
    public List<OmsOrderReturnReason> list(Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        OmsOrderReturnReasonExample example = new OmsOrderReturnReasonExample();
        example.setOrderByClause("sort desc"); // 按照sort条件降序DESC排序
        return returnReasonMapper.selectByExample(example);
    }

    /*
    批量修改退货原因状态
     */
    @Override
    public int updateStatus(List<Long> ids, Integer status) {
        if (!status.equals(0) && !status.equals(1)){
            return 0;
        }
        OmsOrderReturnReason record = new OmsOrderReturnReason();
        record.setStatus(status);
        OmsOrderReturnReasonExample example = new OmsOrderReturnReasonExample();
        example.createCriteria().andIdIn(ids);
        return returnReasonMapper.updateByExampleSelective(record, example);
        /*
        updateByExample是传入一个对象,将整条数据都更新,如果对象中没有值的属性,就自动设置为null.
        updateByExampleSelective(参数一,参数二)是将一行中某几个属性更新,而不改变其他的值
         */
    }

    /*
    获取单个退货原因详情信息
     */
    @Override
    public OmsOrderReturnReason getItem(Long id) {
        return returnReasonMapper.selectByPrimaryKey(id);
    }
}
