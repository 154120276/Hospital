package com.zcj.yygh.user.service;

import com.zcj.yygh.vo.user.LoginVo;

import java.util.Map;

/**
 * @author changjiang
 * @date 2016/10/31
 */

public interface UserInfoService {
    Map<String, Object> loginUser(LoginVo loginVo);
}
