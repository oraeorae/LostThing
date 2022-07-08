package com.security;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//避免跨域问题，本项目暂时没用到
public class Cors {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        // 设置允许跨域请求的域名
                        // 是否允许证书 配置是否允许发送Cookie，用于凭证请求， 默认不发送cookie。
                        .allowCredentials(true)
                        // 设置允许的方法
                        .allowedMethods("GET", "POST", "DELETE", "PUT")
                        .allowedHeaders("Content-Type", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method",
                                "Access-Control-Request-Headers")
                        .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
                        // 设置允许的header属性
                        .allowedHeaders("*")
                        //.allowedOrigins("*")
                        // 跨域允许时间
                        .maxAge(3600);
            }
        };
    }
}
