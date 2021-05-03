package com.zcj.yygh.user.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcj.yygh.common.exception.YyghException;
import com.zcj.yygh.common.helper.JwtHelper;
import com.zcj.yygh.common.result.ResultCodeEnum;
import com.zcj.yygh.model.user.UserInfo;
import com.zcj.yygh.user.mapper.UserInfoMapper;
import com.zcj.yygh.user.service.UserInfoService;
import com.zcj.yygh.vo.user.LoginVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 朱长江
 * @date 2021/5/2
 */
@Service
public class UserInfoServiceImpl  extends
        ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService{

    @Override
    public Map<String, Object> loginUser(LoginVo loginVo) {
        //获取输入手机号和验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        //校验参数
        if(StringUtils.isEmpty(phone) ||
                StringUtils.isEmpty(code)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //判断手机验证码和输入验证码是否一致
        //TODO 校验校验验证码
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        //获取用户
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);
        if(null == userInfo) {
            //添加数据到数据库中
            userInfo = new UserInfo();
            userInfo.setName("");
            userInfo.setPhone(phone);
            userInfo.setStatus(1);
            baseMapper.insert(userInfo);
        }
        //校验是否被禁用
        if(userInfo.getStatus() == 0) {
            throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }



        //返回登录信息
        //返回页面显示名称
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        //jwt生产token
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);
        return map;
    }
}
