package com.itgr.zhaojbackendquestionservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.itgr.zhaojbackendquestionservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.itgr")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.itgr.zhaojbackendserviceclient.service"})
public class ZhaojBackendQuestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhaojBackendQuestionServiceApplication.class, args);
    }

}
