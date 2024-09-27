package com.itgr.zhaojbackendjudgeservice;

import com.itgr.zhaojbackendcommon.utils.ConstantPropertiesUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan(basePackages = "com.itgr",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = ConstantPropertiesUtil.class))
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.itgr.zhaojbackendserviceclient.service"})
public class ZhaojBackendJudgeServiceApplication {

    public static void main(String[] args) {
        // 初始化消息队列，先注释掉，改用 Bean 的方式初始化消息队列（InitRabbitMqBean.java）
//        InitRabbitMq.doInit();
        SpringApplication.run(ZhaojBackendJudgeServiceApplication.class, args);
    }
}
