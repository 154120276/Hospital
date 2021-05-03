package com.zcj.yygh.hosp.service;

import com.zcj.yygh.model.hosp.Hospital;
import com.zcj.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author changjiang
 * @date 2016/10/31
 */

public interface HospitalService {
    //上传医院
    void save(Map<String, Object> requestMap);
    //根据医院编号查询
    Hospital getByHoscode(String hoscode);

    Page<Hospital> selectHospPage(int page, int limit, HospitalQueryVo hospitalQueryVo);

    void undateStatus(String id, Integer status);

    Map<String,Object> getHospById(String id);

    String getHospName(String hoscode);

    List<Hospital> findByHosname(String hosname);

    Map<String, Object> item(String hoscode);
}
