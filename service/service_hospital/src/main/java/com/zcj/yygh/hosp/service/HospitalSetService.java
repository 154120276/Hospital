package com.zcj.yygh.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcj.yygh.model.hosp.HospitalSet;

/**
 * @author 朱长江
 * @date 2021/4/15
 */

public interface HospitalSetService  extends IService<HospitalSet> {
    String getSignKey(String hoscode);
}
