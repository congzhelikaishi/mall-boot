package com.testweb.mall.component;

import com.testweb.mall.service.OmsPortalOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 取消订单消息的消费者
 * 取消订单接收者
 * 取消订单消息的处理者
 */
@Component
@RabbitListener(queues = "mall.order.cancel")  // 定义消息处理器,监听器监听指定队列
/*
在@RabbitListener注解内配置了监听的队列，这里配置内容是QueueEnum枚举内的queueName属性值，当然如果你采用常量的方式在注解属性上是直接可以使用的，枚举不支持这种配置，这里只能把QueueName字符串配置到queues属性上了。
由于我们在消息发送时采用字符串的形式发送消息内容，这里在@RabbitHandler处理方法的参数内要保持数据类型一致
 */
public class CancelOrderReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(CancelOrderReceiver.class);

    @Autowired
    private OmsPortalOrderService portalOrderService;

    @RabbitHandler
    public void handle(Long orderId){
        portalOrderService.cancelOrder(orderId);
        LOGGER.info("取消订单消息队列process orderId:{}", orderId);
    }
}
/*
@RabbitListener注解指定目标方法来作为消费消息的方法，通过注解参数指定所监听的队列或者Binding。使用@RabbitListener可以设置一个自己明确默认值的RabbitListenerContainerFactory对象。
可以在配置文件中设置RabbitListenerAnnotationBeanPostProcessor并通过<rabbit:annotation-driven/>来设置@RabbitListener的执行，当然也可以通过@EnableRabbit注解来启用@RabbitListener。
 */