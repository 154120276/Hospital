package com.zcj.yygh.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcj.yygh.common.result.Result;
import com.zcj.yygh.common.util.MD5;
import com.zcj.yygh.hosp.service.HospitalSetService;
import com.zcj.yygh.model.hosp.HospitalSet;

import com.zcj.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

/**
 * @author 朱长江
 * @date 2021/4/15
 */

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")

public class HospitalSetController {
    /**
     * 注入service
     */
    @Autowired
    private HospitalSetService hospitalSetService;


    /**
     * 1 查询医院设置表所有信息
     * @return 所有信息
     */
    @ApiOperation(value = "获取所有医院设置信息")
    @GetMapping("findAll")
    public Result findAllHospitalSet(){
        //调用service
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    /**
     *  逻辑删除医院设置
     * @param id 医院id
     * @return 是否成功
     */
    @ApiOperation(value = "逻辑删除医院设置信息")
    @DeleteMapping("{id}")
    public Result removeHospitalSet(
            @ApiParam(name="id", value = "医院id",required = true) @PathVariable  Long id){
        boolean b = hospitalSetService.removeById(id);
        return b ? Result.ok() : Result.fail();

    }

    /**
     *  条件查询带分页
     * @param current 当前页
     * @param limit  分页数
     * @param hospitalSetQueryVo 条件实体类
     * @return
     */
    @ApiOperation(value = "条件查询带分页")
    @PostMapping("findPage/{current}/{limit}")
    public Result findPageHospSet(
            @ApiParam(name="current", value = "当前页码",required = true)  @PathVariable long current,
            @ApiParam(name="limit", value = "分页数",required = true)  @PathVariable long limit,
            @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){
        Page<HospitalSet> page = new Page<>(current,limit);
        //构建条件
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        //医院名称
        String hosname = hospitalSetQueryVo.getHosname();
        //医院编号
        String hoscode = hospitalSetQueryVo.getHoscode();
        if(!StringUtils.isEmpty(hosname)) {
            wrapper.like("hosname",hospitalSetQueryVo.getHosname());
        }
        if(!StringUtils.isEmpty(hoscode)) {
            wrapper.eq("hoscode",hospitalSetQueryVo.getHoscode());
        }
        //调用方法实现分页查询
        IPage<HospitalSet> pageHospitalSet = hospitalSetService.page(page, wrapper);
        //返回结果
        return Result.ok(pageHospitalSet);
    }


    /**
     * 添加医院设置
     * @param hospitalSet 医院设置
     * @return
     */
    @ApiOperation(value = "添加医院设置")
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(
            @ApiParam(name="hospitalSet", value = "医院设置信息",required = true) @RequestBody HospitalSet hospitalSet){
        hospitalSet.setStatus(1);
        //签名密钥，用MD5加密
        Random random=new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));
        //调用Service
        boolean save = hospitalSetService.save(hospitalSet);
        return save ? Result.ok() : Result.fail();

    }

    /**
     *  根据id获取医院设置
     * @param id id
     * @return
     */
    @ApiOperation(value = "根据id查询医院设置")
    @GetMapping("getHospSet/{id}")
    public Result getHospSet(
            @ApiParam(name="id", value = "医院编号",required = true)       @PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);

    }

    /**
     * 修改医院设置
     * @param hospitalSet
     * @return
     */
    @ApiOperation(value = "修改医院设置")
    @PostMapping("updateHospitalSet")
    public Result updateHospitalSet(
            @ApiParam(name="hospitalSet", value = "医院设置信息",required = true) @RequestBody HospitalSet hospitalSet){
        boolean flag = hospitalSetService.updateById(hospitalSet);
        return flag ? Result.ok() : Result.fail();
    }


    /**
     *  批量删除
     * @param idList id集合
     * @return
     */
    @ApiOperation(value = "批量删除接口")
    @DeleteMapping("batchRemove")
    public Result batchRemoveHospital(
            @ApiParam(name="idlist", value = "医院设置id集合",required = true) @RequestBody List<Long> idList){
        hospitalSetService.removeByIds(idList);
        return Result.ok();
    }

    /**
     *  医院设置解锁与锁定
     * @param id
     * @param status
     * @return
     */
    @ApiOperation(value = "解锁与锁定")
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lock(
            @ApiParam(name="id", value = "医院设置id",required = true)  @PathVariable String id,
            @ApiParam(name="status", value = "医院状态(0是锁定，1是启用)",required = true) @PathVariable Integer status
    ){
        //根据id查询出医院设置信息
        HospitalSet byId = hospitalSetService.getById(id);
        //设置状态
        byId.setStatus(status);
        //调用修改
        boolean flag = hospitalSetService.updateById(byId);
        return flag ? Result.ok() : Result.fail();

    }


    /**
     *  发送key
     * @param id
     * @return
     */
    @PutMapping("sendKey/{id}")
    @ApiOperation(value = "根据id获取密钥")
    public Result lockHospitalSet(
            @ApiParam(name="id", value = "医院设置id",required = true)  @PathVariable Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        //TODO 发送短信（未实现）
        return Result.ok();
    }
}
