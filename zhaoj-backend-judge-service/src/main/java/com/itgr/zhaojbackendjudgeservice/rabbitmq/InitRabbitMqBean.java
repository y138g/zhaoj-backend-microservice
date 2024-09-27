package com.itgr.zhaojbackendjudgeservice.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 用于创建测试程序用到的交换机和队列（只用在程序启动前执行一次）
 * @author ygking
 */
@Slf4j
@Component
public class InitRabbitMqBean {

    @Value("${spring.rabbitmq.host:rabbitmq}")
    private String host;

    @PostConstruct
    public void init() {
        try {
            log.info("初始化 RabbitMQ...");
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            Connection connection = factory.newConnection();
            log.info("创建 Connection: {}", connection);
            Channel channel = connection.createChannel();
            log.info("创建通道 Channel: {}", channel);
            String EXCHANGE_NAME = "code_exchange";
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            log.info("交换机 Exchange declared: {}", EXCHANGE_NAME);

            String queueName = "code_queue";
            channel.queueDeclare(queueName, true, false, false, null);
            log.info("创建队列 Queue declared: {}", queueName);
            channel.queueBind(queueName, EXCHANGE_NAME, "my_routingKey");
            log.info("队列进入交换机: {} -> {}", queueName, EXCHANGE_NAME);
            log.info("消息队列启动成功");
        } catch (Exception e) {
            log.error("消息队列启动失败", e);
        }
    }
}
