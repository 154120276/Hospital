package com.zcj.yygh.hosp.service;


import com.zcj.yygh.model.hosp.Department;
import com.zcj.yygh.vo.hosp.DepartmentQueryVo;
import com.zcj.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author changjiang
 * @date 2016/10/31
 */

public interface DepartmentService {

    /**
     * 上传科室信息
     * @param requestMap
     */

    void save(Map<String, Object> requestMap);

    Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo);

    void remove(String hoscode, String depcode);

    List<DepartmentVo> findDeptTree(String hoscode);

    String getDepName(String hoscode, String depcode);
}
