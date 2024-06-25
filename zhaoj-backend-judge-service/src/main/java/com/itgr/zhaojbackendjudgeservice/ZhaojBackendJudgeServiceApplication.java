package com.itgr.zhaojbackendjudgeservice;

import com.itgr.zhaojbackendjudgeservice.rabbitmq.InitRabbitMq;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.itgr")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.itgr.zhaojbackendserviceclient.service"})
public class ZhaojBackendJudgeServiceApplication {

    public static void main(String[] args) {
        // 初始化消息队列
        InitRabbitMq.doInit();
        SpringApplication.run(ZhaojBackendJudgeServiceApplication.class, args);
    }

}
