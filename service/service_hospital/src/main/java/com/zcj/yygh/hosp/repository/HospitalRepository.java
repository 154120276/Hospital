package com.zcj.yygh.hosp.repository;

import com.zcj.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author 朱长江
 * @date 2021/4/19
 */

public interface HospitalRepository extends MongoRepository<Hospital,String> {

   //判断是否存在数据
    Hospital getHospitalByHoscode(String hoscode);


    List<Hospital> findHospitalByHosnameLike(String hosname);
}
