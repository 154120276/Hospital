package com.zcj.yygh.cmn.controller;

import com.zcj.yygh.cmn.service.DictService;
import com.zcj.yygh.common.result.Result;
import com.zcj.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author 朱长江
 * @date 2021/4/17
 */

@Api(value = "数据字典查询")
@RestController
@RequestMapping(value = "/admin/cmn/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    /**
     * 根据数据id查询其子数据列表
     * @param id id
     * @return
     */
    @ApiOperation(value = "根据数据id查询其子数据列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(
            @ApiParam(name="id", value = "医院设置id",required = true)  @PathVariable Long id){
           List<Dict> list= dictService.findChlidData(id);
           return Result.ok(list);
    }

    /**
     *  导出数据字典
     * @param response
     */
    @ApiOperation(value = "导出数据字典")
    @GetMapping("exportData")
    public void exportData(HttpServletResponse response){
        dictService.exportDictData(response);

    }

    /**
     * 导入数据字典
     * @param file
     * @return
     */
    @ApiOperation(value = "导入数据字典")
    @PostMapping("importData")
    public Result importDict(MultipartFile file){
        dictService.importDictData(file);
        return Result.ok();
    }

    /**
     * 根据dictcode和value查询名称
     */

    @ApiOperation(value = "根据dictcode和value查询")
    @GetMapping("getName/{dictCode}/{value}")
    public String getName(
        @PathVariable String dictCode,
        @PathVariable String value){
        return dictService.getDictName(dictCode,value);
    }

    /**
     * 根据value查名称
     * @param value
     * @return
     */
    @ApiOperation(value = "根据value查询")
    @GetMapping("getName/{value}")
    public String getName(
            @PathVariable String value){
        return dictService.getDictName("",value);
    }

    @ApiOperation(value = "根据dictcode查询下级节点")
    @GetMapping("findByDictCode/{dictCode}")
    public Result findByDictCode(
            @PathVariable String dictCode){
        List<Dict> list=dictService.findByDictCode(dictCode);
        return Result.ok(list);

    }
}
