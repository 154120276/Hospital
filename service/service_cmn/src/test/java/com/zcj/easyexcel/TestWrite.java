package com.zcj.easyexcel;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 朱长江
 * @date 2021/4/18
 */

public class TestWrite {


    public static void main(String[] args) {
        //构建数据list集合
        List<UserData> list=new ArrayList<>();
        for (int i = 0; i <10; i++) {
            UserData data=new UserData();
            data.setUid(i);
            data.setUsername("lucy"+i);
            list.add(data);
        }

        //设置Excel文件路径和文件名称
        String filename="D:\\xiangmu\\yygh_parent\\excel\\01.xlsx";
        //调用方法实现写操作
        EasyExcel.write(filename,UserData.class).sheet("用户信息")
        .doWrite(list);
    }
}
