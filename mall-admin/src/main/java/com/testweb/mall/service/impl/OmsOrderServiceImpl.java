package com.testweb.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.testweb.mall.dao.OmsOrderDao;
import com.testweb.mall.dao.OmsOrderOperateHistoryDao;
import com.testweb.mall.dto.*;
import com.testweb.mall.mapper.OmsOrderMapper;
import com.testweb.mall.mapper.OmsOrderOperateHistoryMapper;
import com.testweb.mall.model.OmsOrder;
import com.testweb.mall.model.OmsOrderExample;
import com.testweb.mall.model.OmsOrderOperateHistory;
import com.testweb.mall.service.OmsOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class OmsOrderServiceImpl implements OmsOrderService {

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private OmsOrderDao orderDao;

    @Autowired
    private OmsOrderOperateHistoryDao orderOperateHistoryDao;

    @Autowired
    private OmsOrderOperateHistoryMapper orderOperateHistoryMapper;

    /*
    订单查询
     */
    @Override
    public List<OmsOrder> list(OmsOrderQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return orderDao.getList(queryParam);
    }

    /*
    批量发货
     */
    @Override
    public int delivery(List<OmsOrderDeliveryParam> deliveryParamList) {
        // 批量发货
        int count = orderDao.delivery(deliveryParamList); // 包含订单id，物流公司，物流单号
        // 添加操作记录
        List<OmsOrderOperateHistory> operateHistoryList = deliveryParamList.stream()
                .map(omsOrderDeliveryParam -> { // 1、Stream中of方法传入可变参数  2、Stream中map元素类型转化方法,里面包含function  3、Function匿名接口，自定义类匿名对象的使用  4、Lambda表达式
                    OmsOrderOperateHistory history = new OmsOrderOperateHistory();
                    history.setOrderId(omsOrderDeliveryParam.getOrderId());
                    history.setCreateTime(new Date());
                    history.setOperateMan("后台管理员");
                    history.setNote("完成发货");
                    return history;
                }).collect(Collectors.toList()); // Collectors 类实现了很多归约操作，例如将流转换成集合和聚合元素。Collectors 可用于返回列表或字符串
        orderOperateHistoryDao.insertList(operateHistoryList); // 批量创建操作
        return count;
    }

    /*
    批量关闭订单
     */
    @Override
    public int close(List<Long> ids, String note) {
        OmsOrder record = new OmsOrder();
        record.setStatus(4);
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andDeleteStatusEqualTo(0).andIdIn(ids); // 条件status为0，以ids列表批量操作
        int count = orderMapper.updateByExampleSelective(record, example);
        List<OmsOrderOperateHistory> historyList = ids.stream()
                .map(orderId -> {
                    OmsOrderOperateHistory history = new OmsOrderOperateHistory();
                    history.setOrderId(orderId);
                    history.setCreateTime(new Date());
                    history.setOperateMan("后台管理员");
                    history.setOrderStatus(4);
                    history.setNote("订单关闭"+note);
                    return history;
                }).collect(Collectors.toList());
        orderOperateHistoryDao.insertList(historyList);
        return count;

    }

    /*
    批量删除订单
     */
    @Override
    public int delete(List<Long> ids) {
        OmsOrder record = new OmsOrder();
        record.setDeleteStatus(1);
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andDeleteStatusEqualTo(0).andIdIn(ids);
        return orderMapper.updateByExampleSelective(record, example);
    }

    /*
    获取指定订单详情
     */
    @Override
    public OmsOrderDetail detail(Long id) {
        return orderDao.getDetail(id);
    }

    /*
    修改订单收货人信息
     */
    @Override
    public int updateReceiverInfo(OmsReceiverInfoParam receiverInfoParam) {
        OmsOrder order = new OmsOrder();
        order.setId(receiverInfoParam.getOrderId());
        order.setReceiverName(receiverInfoParam.getReceiverName());
        order.setReceiverPhone(receiverInfoParam.getReceiverPhone());
        order.setReceiverPostCode(receiverInfoParam.getReceiverPostCode());
        order.setReceiverDetailAddress(receiverInfoParam.getReceiverDetailAddress());
        order.setReceiverCity(receiverInfoParam.getReceiverCity());
        order.setReceiverRegion(receiverInfoParam.getReceiverRegion());
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        // 插入操作
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(receiverInfoParam.getOrderId());
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(receiverInfoParam.getStatus());
        history.setNote("修改收货人信息");
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    /*
    修改订单费用信息
     */
    @Override
    public int updateMoneyInfo(OmsMoneyInfoParam moneyInfoParam) {
        OmsOrder order = new OmsOrder();
        order.setId(moneyInfoParam.getOrderId());
        order.setFreightAmount(moneyInfoParam.getFreightAmount());
        order.setDiscountAmount(moneyInfoParam.getDiscountAmount());
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        // 插入操作记录
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(moneyInfoParam.getOrderId());
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(moneyInfoParam.getStatus());
        history.setNote("修改费用信息");
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    /*
    修改订单备注
     */
    @Override
    public int updateNote(Long id, String note, Integer status) {
        OmsOrder order = new OmsOrder();
        order.setId(id);
        order.setNote(note);
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(id);
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(status);
        history.setNote("修改备注信息：" + note);
        orderOperateHistoryMapper.insert(history);
        return count;
    }
}
