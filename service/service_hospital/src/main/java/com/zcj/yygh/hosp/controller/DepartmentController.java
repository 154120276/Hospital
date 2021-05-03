package com.zcj.yygh.hosp.controller;

import com.zcj.yygh.common.result.Result;
import com.zcj.yygh.hosp.service.DepartmentService;
import com.zcj.yygh.vo.hosp.DepartmentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 朱长江
 * @date 2021/4/27
 */

@Api(tags = "医院科室管理")
@RestController
@RequestMapping("/admin/hosp/department")

public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @ApiOperation(value = "查询医院所有科室列表")
    @GetMapping("getDeptList/{hoscode}")
    public Result getDeptList(
           @ApiParam(name="hoscode", value = "医院id",required = true) @PathVariable String hoscode
    ){
        List<DepartmentVo> list=departmentService.findDeptTree(hoscode);
        return Result.ok(list);
    }

}
