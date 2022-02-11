package com.testweb.mall.component;

import com.testweb.mall.service.OmsPortalOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 订单超时取消并解锁库存的定时器
 */
@Component
public class OrderTimeOutCancelTask {
    private Logger LOGGER = LoggerFactory.getLogger(OrderTimeOutCancelTask.class);
    @Autowired
    private OmsPortalOrderService portalOrderService;

    /**
     * cron表达式：Seconds Minutes Hours DayofMonth Month DayofWeek [Year]
     * 每10分钟扫描一次，扫描设定超时时间之前下的订单，如果没支付则取消该订单
     */
    @Scheduled(cron = "0 0/10 * ? * ?")  // 通过 Spring 提供的 @Scheduled 注解即可定义定时任务 每十分钟执行一次
    private void cancelTimeOutOrder(){
        Integer count = portalOrderService.cancelTimeOutOrder();
        LOGGER.info("取消订单，并根据sku编号释放锁定库存，取消订单数量：{}",count);
    }
    /*
    Cron格式中每个时间元素的说明
    时间元素	    可出现的字符	        有效数值范围
    Seconds	    , - * /	            0-59
    Minutes	    , - * /	            0-59
    Hours	    , - * /	            0-23
    DayofMonth	, - * / ? L W	    0-31
    Month	    , - * /	            1-12
    DayofWeek	, - * / ? L #	    1-7或SUN-SAT
     */
}
