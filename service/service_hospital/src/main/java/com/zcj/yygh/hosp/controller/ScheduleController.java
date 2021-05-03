package com.zcj.yygh.hosp.controller;

import com.zcj.yygh.common.result.Result;
import com.zcj.yygh.hosp.service.ScheduleService;
import com.zcj.yygh.model.hosp.HospitalSet;
import com.zcj.yygh.model.hosp.Schedule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author 朱长江
 * @date 2021/4/27
 */


@RestController
@RequestMapping("/admin/hosp/schedule")
@Api(tags = "排班管理")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 根据医院编号 和科室编号，查询排版规则数据
     * @return
     */
    @ApiOperation(value = "查询排班规则数据")
    @GetMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getScheduleRule(
            @ApiParam(name="page", value = "当前页",required = true) @PathVariable long page,
            @ApiParam(name="limit", value = "每页数",required = true)@PathVariable long limit,
            @ApiParam(name="hoscode", value = "医院编号",required = true)@PathVariable String hoscode,
            @ApiParam(name="depcode", value = "科室编号",required = true) @PathVariable String depcode
    ){
      Map<String,Object> map=scheduleService.getRuleSchedule(page,limit,hoscode,depcode);
        return Result.ok(map);
    }

    /**
     * 根据医院编号、科室编号和工作日期，查询排班详细信息
     * @param hoscode
     * @param depcode
     * @param workDate
     * @return
     */
    @ApiOperation(value = "查询排班详细信息")
    @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result getScheduleDetail(
            @ApiParam(name="hoscode", value = "医院编号",required = true)@PathVariable String hoscode,
            @ApiParam(name="depcode", value = "科室编号",required = true) @PathVariable String depcode,
            @ApiParam(name="workDate", value = "工作日期",required = true)@PathVariable String workDate
    ){
       List<Schedule> list=scheduleService.getDetailSchedule(hoscode,depcode,workDate);
        return Result.ok(list);
    }
}
