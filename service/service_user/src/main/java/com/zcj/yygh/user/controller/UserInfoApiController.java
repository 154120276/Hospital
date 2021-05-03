package com.zcj.yygh.user.controller;

import com.zcj.yygh.common.result.Result;
import com.zcj.yygh.user.service.UserInfoService;
import com.zcj.yygh.vo.user.LoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author 朱长江
 * @date 2021/5/2
 */

@RestController
@RequestMapping("/api/user")
@Api(tags = "用户信息api")
public class UserInfoApiController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 用户登录
     * @param loginVo
     * @return
     */
    @ApiOperation(value = "用户登录")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo){
        Map<String, Object> info=userInfoService.loginUser(loginVo);
        return Result.ok(info);
    }
}
