package com.zcj.yygh.hosp.controller.Api;

import com.zcj.yygh.common.result.Result;
import com.zcj.yygh.hosp.service.DepartmentService;
import com.zcj.yygh.hosp.service.HospitalService;
import com.zcj.yygh.model.hosp.Hospital;
import com.zcj.yygh.vo.hosp.DepartmentVo;
import com.zcj.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author 朱长江
 * @date 2021/4/28
 */


@RestController
@RequestMapping("/api/hosp/hospital")
@Api(tags = "HospApi接口")
public class HospApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    /**
     * 查询医院列表功能
     * @param page
     * @param limit
     * @param hospitalQueryVo
     * @return
     */
    @ApiOperation(value = "查询医院列表功能")
    @GetMapping("findHospList/{page}/{limit}")
    public Result findHospList(
            @PathVariable int page,
            @PathVariable int limit,
            HospitalQueryVo hospitalQueryVo
    ){
        Page<Hospital> hospitals = hospitalService.selectHospPage(page, limit, hospitalQueryVo);
        return Result.ok(hospitals);
    }

    /**
     * 根据医院名称模糊查询
     * @param hosname
     * @return
     */
    @ApiOperation(value = "根据医院名称模糊查询")
    @GetMapping("findByHosname/{hosname}")
    public Result findByHosName(
            @ApiParam(name="hosname", value = "医院名称",required = true) @PathVariable String hosname
    ){
     List<Hospital> list=hospitalService.findByHosname(hosname);
        return Result.ok(list);
    }

    /**
     * 根据医院编号获取所有科室信息
     * @param hoscode
     * @return
     */
    @ApiOperation(value = "根据医院编号获取所有科室信息")
    @GetMapping("department/{hoscode}")
    public Result index(
            @ApiParam(name="hoscod", value = "医院编号",required = true) @PathVariable String hoscode
    ){
        List<DepartmentVo> deptTree = departmentService.findDeptTree(hoscode);
        return Result.ok(deptTree);
    }

    /**
     * 根据医院编号获取预约挂号详情
     * @param hoscode
     * @return
     */
    @ApiOperation(value = "根据医院编号获取预约挂号详情")
    @GetMapping("findHospDetail/{hoscode}")
    public Result item(
            @ApiParam(name="hoscode", value = "医院编号",required = true) @PathVariable String hoscode
    ){
        Map<String,Object> map= hospitalService.item(hoscode);
        return Result.ok(map);
    }

}
