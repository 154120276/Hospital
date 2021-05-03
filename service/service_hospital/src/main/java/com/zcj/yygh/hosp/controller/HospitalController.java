package com.zcj.yygh.hosp.controller;

import com.zcj.yygh.common.result.Result;
import com.zcj.yygh.hosp.service.HospitalService;
import com.zcj.yygh.model.hosp.Hospital;
import com.zcj.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author 朱长江
 * @date 2021/4/26
 */

@Api(tags = "医院列表管理")
@RestController
@RequestMapping("/admin/hosp/hospital")

public class HospitalController {

    @Autowired
    private HospitalService hospitalService;


    /**
     * 医院列表（条件查询分页）
     * @param page
     * @param limit
     * @param hospitalQueryVo
     * @return
     */
    @ApiOperation(value = "医院列表（条件查询分页）")
    @GetMapping("list/{page}/{limit}")
    public Result listHosp(
            @PathVariable Integer page,
            @PathVariable Integer limit,
            HospitalQueryVo hospitalQueryVo){
       Page<Hospital> pagemodel=hospitalService.selectHospPage(page,limit,hospitalQueryVo);
        return Result.ok(pagemodel);
    }

    /**
     * 更新医院状态方法
     * @param id
     * @param status
     * @return
     */
    @ApiOperation(value = "更新医院状态方法")
    @GetMapping("updateHospStatus/{id}/{status}")
    public Result updateHospStatus(
            @ApiParam(name="id", value = "医院id",required = true)   @PathVariable String id,
            @ApiParam(name="status", value = "医院上线状态",required = true) @PathVariable Integer status
            ){
        hospitalService.undateStatus(id,status);
        return Result.ok();
    }

    /**
     * 医院详情信息
     * @param id
     * @return
     */
    @ApiOperation(value = "医院详情信息")
    @GetMapping("showHospDetail/{id}")
    public Result showHospDetail(
            @ApiParam(name="id", value = "医院id",required = true)   @PathVariable String id
    ){
        Map<String,Object> map=hospitalService.getHospById(id);
        return Result.ok(map);
    }
}
