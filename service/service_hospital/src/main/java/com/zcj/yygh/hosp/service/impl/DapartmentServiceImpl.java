package com.zcj.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zcj.yygh.hosp.repository.DepartmentRepository;
import com.zcj.yygh.hosp.service.DepartmentService;
import com.zcj.yygh.model.hosp.Department;
import com.zcj.yygh.vo.hosp.DepartmentQueryVo;
import com.zcj.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 朱长江
 * @date 2021/4/20
 */

@Service
public class DapartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    /**
     * 上传科室接口
     * @param requestMap
     */
    @Override
    public void save(Map<String, Object> requestMap) {
        //paramMap-->department对象
        String s = JSONObject.toJSONString(requestMap);
        Department department = JSONObject.parseObject(s,Department.class);
        //根据医院编号和科室编号查询
        Department departmentExist = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());
        //判断
        if (departmentExist!=null){
            departmentExist.setUpdateTime(new Date());
            departmentExist.setIsDeleted(0);
            departmentRepository.save(departmentExist);
        }else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    /**
     * 分页查询科室列表
     * @param page
     * @param limit
     * @param departmentQueryVo
     * @return
     */
    @Override
    public Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo) {
        //创建pageable对象
        Pageable pageable= PageRequest.of(page-1,limit);
        Department department=new Department();
        BeanUtils.copyProperties(departmentQueryVo,department);
        //创建Example对象
        ExampleMatcher matcher=ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING).withIgnoreCase(true);
        Example<Department> example=Example.of(department,matcher);
        Page<Department> all = departmentRepository.findAll(example, pageable);
        return all;
    }

    /**
     * 删除医院接口
     * @param hoscode
     * @param depcode
     */
    @Override
    public void remove(String hoscode, String depcode) {
        //根据医院编号和科室编号查询
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department!=null){
            //调用方法删除
            departmentRepository.deleteById(department.getId());
        }
    }

    /**
     * 根据医院编号，查询所有科室列表
     * @param hoscode
     * @return
     */
    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
       //川剧一个list集合，用于最终数据封装
        List<DepartmentVo> result=new ArrayList<>();
        //根据医院编号，查询医院所有科室信息
        Department departmentQuery=new Department();
        departmentQuery.setHoscode(hoscode);
        Example<Department> example = Example.of(departmentQuery);
       //所有科室列表信息
        List<Department> all = departmentRepository.findAll(example);
        //根据大科室编号 bigcode 分组，获取每个大科室里面下级科室
        Map<String, List<Department>> deparmentMap = all.stream().collect(Collectors.groupingBy(Department::getBigcode));
        //遍历map集合
        for (Map.Entry<String,List<Department>> entry : deparmentMap.entrySet()){
            String bigcode=entry.getKey();
            //大科室编号对应的全局数据
            List<Department> departments = entry.getValue();
            //封装大科室
            DepartmentVo department1Vo=new DepartmentVo();
            department1Vo.setDepcode(bigcode);
            department1Vo.setDepname(departments.get(0).getBigname());
            //封装小科室
            List<DepartmentVo> children=new ArrayList<>();
            for(Department department : departments){
                DepartmentVo department2Vo=new DepartmentVo();
                department2Vo.setDepcode(department.getDepcode());
                department2Vo.setDepname(department.getDepname());
                //封装到list集合
                children.add(department2Vo);
            }
            //吧小科室放进大科室里面
            department1Vo.setChildren(children);
            //放入result里面
            result.add(department1Vo);
        }

        return result;
    }

    //根据科室编号和医院编号查询科室名称
    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department!=null){
            return department.getDepname();
        }
        return null;
    }


}
