package com.zcj.easyexcel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * easyexcel 测试
 * @author 朱长江
 * @date 2021/4/18
 */

@Data
public class UserData {

    @ExcelProperty("用户编号")
    private int uid;

    @ExcelProperty("用户名称")
    private String username;
}
