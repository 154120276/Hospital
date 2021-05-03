package com.zcj.yygh.hosp;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author 朱长江
 * @date 2021/4/14
 */

@SpringBootApplication
@ComponentScan(basePackages = "com.zcj")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.zcj")
public class ServiceHospApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospApplication.class, args);
    }

}
