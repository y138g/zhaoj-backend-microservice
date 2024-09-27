package com.itgr.zhaojbackendquestionservice;

import com.itgr.zhaojbackendcommon.utils.ConstantPropertiesUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.itgr.zhaojbackendquestionservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan(basePackages = "com.itgr",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = ConstantPropertiesUtil.class))
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.itgr.zhaojbackendserviceclient.service"})
public class ZhaojBackendQuestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhaojBackendQuestionServiceApplication.class, args);
    }

}
