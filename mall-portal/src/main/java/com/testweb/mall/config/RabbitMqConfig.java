package com.testweb.mall.config;

import com.testweb.mall.domain.QueueEnum;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列相关配置
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 订单消息实际消费队列所绑定的交换机（处理订单是否取消的交换机）
     */
    @Bean
    DirectExchange orderDirect() {
        // durable(true) 持久化，mq重启之后交换机还在
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_ORDER_CANCEL.getExchange())  // 声明一个Direct的交换机
                .durable(true)
                .build();
    }
    /*
    durable： 是否持久化, 队列的声明默认是存放到内存中的，如果rabbitmq重启会丢失，
    如果想重启之后还存在就要使队列持久化，保存到Erlang自带的Mnesia数据库中，当rabbitmq重启之后会读取该数据库
     */

    /**
     * 订单延迟队列队列所绑定的交换机（下单后存入交换机）
     */
    @Bean
    DirectExchange orderTtlDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getExchange())  // 声明一个Direct的交换机
                .durable(true)
                .build();
    }

    /**
     * 订单实际消费队列（处理订单是否取消队列）
     */
    @Bean
    public Queue orderQueue() {
        return new Queue(QueueEnum.QUEUE_ORDER_CANCEL.getName());
    }

    /**
     * 订单延迟队列（死信队列）
     */
    @Bean
    public Queue orderTtlQueue() {
        return QueueBuilder
                .durable(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getName())
                //声明该队列的死信消息发送到的 交换机 （队列添加了这个参数之后会自动与该交换机绑定，并设置路由键，不需要开发者手动设置)
                .withArgument("x-dead-letter-exchange", QueueEnum.QUEUE_ORDER_CANCEL.getExchange())  // 到期后转发的交换机

                //声明该队列死信消息在交换机的 路由键
                .withArgument("x-dead-letter-routing-key", QueueEnum.QUEUE_ORDER_CANCEL.getRouteKey())  // 到期后转发的路由键
                .build();
    }

    /**
     * 将订单队列绑定到交换机
     */
    @Bean
    Binding orderBinding(DirectExchange orderDirect,Queue orderQueue){
        return BindingBuilder
                .bind(orderQueue)  // 绑定队列
                .to(orderDirect)  // 指定交换机
                .with(QueueEnum.QUEUE_ORDER_CANCEL.getRouteKey());  // 路由规则
    }

    /**
     * 将订单延迟队列绑定到交换机
     */
    @Bean
    Binding orderTtlBinding(DirectExchange orderTtlDirect,Queue orderTtlQueue){
        return BindingBuilder
                .bind(orderTtlQueue)  // 绑定队列
                .to(orderTtlDirect)  // 指定交换机
                .with(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getRouteKey());  // 路由规则
    }

}