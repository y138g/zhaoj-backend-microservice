package com.itgr.zhaojbackendgateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Arrays;

/**
 * 处理跨域
 * TODO 此处为本地测试用，线上部署时请更改配置
 */
@Configuration
public class LocalCorsConfig {

    private static final String LOCAL_DOMAIN = "http://localhost:8080/";
    private static final String LOCAL_URL = "http://127.0.0.1:8080/";

    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        // 实际/测试域名和IP
        config.setAllowedOriginPatterns(Arrays.asList(LOCAL_URL, LOCAL_DOMAIN));
        config.addAllowedHeader("*");
        config.addExposedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}
