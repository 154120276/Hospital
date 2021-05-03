package com.zcj.yygh.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author 朱长江
 * @date 2021/4/14
 */

@ComponentScan("com.zcj")
@MapperScan("com.zcj.yygh.user.mapper")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.zcj")
@SpringBootApplication
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
