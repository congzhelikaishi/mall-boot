package com.testweb.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.testweb.mall.dao.OmsOrderReturnApplyDao;
import com.testweb.mall.dto.OmsOrderReturnApplyResult;
import com.testweb.mall.dto.OmsReturnApplyQueryParam;
import com.testweb.mall.dto.OmsUpdateStatusParam;
import com.testweb.mall.mapper.OmsOrderReturnApplyMapper;
import com.testweb.mall.model.OmsOrderReturnApply;
import com.testweb.mall.model.OmsOrderReturnApplyExample;
import com.testweb.mall.service.OmsOrderReturnApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
/**
 * 订单退货管理Service实现类
 */
@Service
public class OmsOrderReturnApplyServiceImpl implements OmsOrderReturnApplyService {

    @Autowired
    private OmsOrderReturnApplyDao returnApplyDao;

    @Autowired
    private OmsOrderReturnApplyMapper returnApplyMapper;

    /*
    分页查询申请
     */
    @Override
    public List<OmsOrderReturnApply> list(OmsReturnApplyQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);// 每次查询pageNum条,数量pageSize
        return returnApplyDao.getList(queryParam);
    }

    /*
    批量删除申请
     */
    @Override
    public int delete(List<Long> ids) {
        OmsOrderReturnApplyExample example = new OmsOrderReturnApplyExample();
        example.createCriteria().andIdIn(ids).andStatusEqualTo(3); // 通过Example对象的.createCriteria().andIdIn(ids)方法进行批量处理， andStatusEqualTo(3)条件为3的
        return returnApplyMapper.deleteByExample(example); // 执行删除操作
    }

    /*
    修改申请状态
     */
    @Override
    public int updateStatus(Long id, OmsUpdateStatusParam statusParam) {
        Integer status = statusParam.getStatus();
        OmsOrderReturnApply returnApply = new OmsOrderReturnApply();
        if (status.equals(1)){
            //确认退货
            returnApply.setId(id);
            returnApply.setStatus(1);
            returnApply.setReturnAmount(statusParam.getReturnAmount()); //确认退款金额
            returnApply.setCompanyAddressId(statusParam.getCompanyAddressId());// 收获地址关联id
            returnApply.setHandleTime(new Date());
            returnApply.setHandleMan(statusParam.getHandleMan());
            returnApply.setHandleNote(statusParam.getHandleNote());
        }else if (status.equals(2)){
            //完成退货
            returnApply.setId(id);
            returnApply.setStatus(2);
            returnApply.setHandleTime(new Date());
            returnApply.setHandleMan(statusParam.getHandleMan());
            returnApply.setHandleNote(statusParam.getHandleNote());
        }else if (status.equals(3)) {
            //拒绝退货
            returnApply.setId(id);
            returnApply.setStatus(3);
            returnApply.setHandleTime(new Date());
            returnApply.setHandleMan(statusParam.getHandleMan());
            returnApply.setHandleNote(statusParam.getHandleNote());
        }else{
            return 0;
        }
        return returnApplyMapper.updateByPrimaryKeySelective(returnApply);
    }

    /*
    获取指定申请详情
     */
    @Override
    public OmsOrderReturnApplyResult getItem(Long id) {
        return returnApplyDao.getDetail(id);
    }
}
