package com.zcj.yygh.hosp.controller.Api;

import com.zcj.yygh.hosp.service.ScheduleService;
import com.zcj.yygh.model.hosp.Schedule;
import com.zcj.yygh.vo.hosp.ScheduleQueryVo;
import io.swagger.annotations.Api;
import org.springframework.data.domain.Page;
import com.zcj.yygh.common.exception.YyghException;
import com.zcj.yygh.common.helper.HttpRequestHelper;
import com.zcj.yygh.common.result.Result;
import com.zcj.yygh.common.result.ResultCodeEnum;
import com.zcj.yygh.common.util.MD5;
import com.zcj.yygh.hosp.service.DepartmentService;
import com.zcj.yygh.hosp.service.HospitalService;
import com.zcj.yygh.hosp.service.HospitalSetService;
import com.zcj.yygh.model.hosp.Department;
import com.zcj.yygh.model.hosp.Hospital;
import com.zcj.yygh.vo.hosp.DepartmentQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 朱长江
 * @date 2021/4/19
 */

@Api(tags = "医院设置")
@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 上传医院接口
     * @param request
     * @return
     */
    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> requestMap = HttpRequestHelper.switchMap(parameterMap);
        //获取传递过来的签冥并进行MD5加密
        String hospSign=(String)requestMap.get("sign");
        //根据传递过来的医院编号查询数据库，查询签名
        String hoscode = (String)requestMap.get("hoscode");
        String signKey= hospitalSetService.getSignKey(hoscode);
        //把数据库查询签名进行MD5加密
        String signkeymd5 = MD5.encrypt(signKey);

        //判断签名是否一致
        if(!hospSign.equals(signkeymd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //传输过程中“+”转换为了“ ”，因此我们要转换回来
        String logoData = (String)requestMap.get("logoData");
        logoData = logoData.replaceAll(" ","+");
        requestMap.put("logoData",logoData);

        //调用service的方法
        hospitalService.save(requestMap);
        return Result.ok();

    }

    /**
     * 查询医院
     * @return
     */
    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> requestMap = HttpRequestHelper.switchMap(parameterMap);
        //获取医院编号
        String hoscode=(String)requestMap.get("hoscode");
        //获取传递过来的签名并进行MD5加密
        String hospSign=(String)requestMap.get("sign");
        //根据传递过来的医院编号查询数据库，查询签名
        String signKey= hospitalSetService.getSignKey(hoscode);
        //把数据库查询签名进行MD5加密
        String signkeymd5 = MD5.encrypt(signKey);

        //判断签名是否一致
        if(!hospSign.equals(signkeymd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        //调用service方法实现根据医院编号查询
        Hospital hospital=hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);
    }

    /**
     * 上传科室接口
     * @param request
     * @return
     */
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> requestMap = HttpRequestHelper.switchMap(parameterMap);
        //获取医院编号
        String hoscode=(String)requestMap.get("hoscode");
        //获取传递过来的签冥并进行MD5加密
        String hospSign=(String)requestMap.get("sign");
        //根据传递过来的医院编号查询数据库，查询签名
        String signKey= hospitalSetService.getSignKey(hoscode);
        //把数据库查询签名进行MD5加密
        String signkeymd5 = MD5.encrypt(signKey);
        //判断签名是否一致
        if(!hospSign.equals(signkeymd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //调用service
        departmentService.save(requestMap);
        return  Result.ok();

    }

    /**
     * 查询科室接口
     * @param request
     * @return
     */
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> requestMap = HttpRequestHelper.switchMap(parameterMap);
        //获取医院编号
        String hoscode=(String)requestMap.get("hoscode");
        //当前页 和 每页记录
        int page=StringUtils.isEmpty(requestMap.get("page")) ?
                1 : Integer.parseInt((String)requestMap.get("page"));
        int limit=StringUtils.isEmpty(requestMap.get("limit")) ?
                1 : Integer.parseInt((String)requestMap.get("limit"));



        DepartmentQueryVo departmentQueryVo=new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        //调用service
        Page<Department> pageModel=departmentService.findPageDepartment(page,limit,departmentQueryVo);
        return Result.ok(pageModel);
        }

    /**
     * 删除科室接口
     * @param request
     * @return
     */
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> requestMap = HttpRequestHelper.switchMap(parameterMap);
        //医院编号 和 科室编号
        String hoscode=(String)requestMap.get("hoscode");
        String depcode=(String)requestMap.get("depcode");



        departmentService.remove(hoscode,depcode);
        return Result.ok();
    }

    /**
     * 上传排班接口
     * @param request
     * @return
     */
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> requestMap = HttpRequestHelper.switchMap(parameterMap);
        scheduleService.save(requestMap);
        return Result.ok();
    }

    /**
     * 查询排班接口
     * @param request
     * @return
     */
    @PostMapping("schedule/list")
    public Result findSchedule( HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //医院编号
        String hoscode = (String)paramMap.get("hoscode");

        //科室编号
        String depcode = (String)paramMap.get("depcode");
        //当前页 和 每页记录数
        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String)paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 1 : Integer.parseInt((String)paramMap.get("limit"));

        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);
        //调用service方法
        Page<Schedule> pageModel = scheduleService.findPageSchedule(page,limit,scheduleQueryVo);
        return Result.ok(pageModel);
    }

    /**
     * 删除排班
     * @param request
     * @return
     */
    @PostMapping("schedule/remove")
    public Result remove(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //医院编号
        String hoscode = (String)paramMap.get("hoscode");
        String hosScheduleId=(String)paramMap.get("hosScheduleId");


        scheduleService.remove(hoscode,hosScheduleId);
        return Result.ok();

    }
    }

