package com.zcj.yygh.hosp.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author 朱长江
 * @date 2021/4/15
 */

@Configuration
@MapperScan("com.zcj.yygh.hosp.mapper")
public class HospConfig {
}
