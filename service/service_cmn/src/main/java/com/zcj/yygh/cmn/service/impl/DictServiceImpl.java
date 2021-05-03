package com.zcj.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcj.yygh.cmn.listener.DictListener;
import com.zcj.yygh.cmn.mapper.DictMapper;
import com.zcj.yygh.cmn.service.DictService;
import com.zcj.yygh.model.cmn.Dict;
import com.zcj.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 朱长江
 * @date 2021/4/17
 */

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    /**
     * 根据id查询子数据列表
     * @param id 医院id
     *
     * @return
     */
    @Override
    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    public List<Dict> findChlidData(Long id) {
        QueryWrapper<Dict> wrapper=new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        List<Dict> dicts = baseMapper.selectList(wrapper);
        //向list集合中每个dict对象中设置haschildren值
        for (Dict dict : dicts) {
            Long dictId= dict.getId();
            boolean flag = this.hasChildren(dictId);
            dict.setHasChildren(flag);
        }
        return dicts;
    }


    /**
     * 导出数据到excel表格
     * @param response
     */
    @Override
    public void exportDictData(HttpServletResponse response) {
        //设置下载信息
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = "dict";
        response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");
        //查询出数据
        List<Dict> dicts = baseMapper.selectList(null);
        List<DictEeVo> list=new ArrayList<>();
        //dict---dictEevo
        for (Dict dict : dicts) {
            DictEeVo dictEeVo=new DictEeVo();
            BeanUtils.copyProperties(dict,dictEeVo);
            list.add(dictEeVo);

        }
        //调用EasyExcel
        try {
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("dict").doWrite(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导入数据字典
     * @param file
     */
    @Override
    @CacheEvict(value = "dict",allEntries = true)
    public void importDictData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),DictEeVo.class,new DictListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据dictcode和value找到数据字典
     * @param dictCode
     * @param value
     * @return
     */
    @Override
    public String getDictName(String dictCode, String value) {
        //如果dictCode为空，直接根据value查询
        if(StringUtils.isEmpty(dictCode)){
            QueryWrapper<Dict> wrapper=new QueryWrapper<>();
            wrapper.eq("value",value);
            Dict dict = baseMapper.selectOne(wrapper);
            return dict.getName();
        }else {
           //根据dictcode查询dict对象，得到id值
            QueryWrapper<Dict> wrapper=new QueryWrapper<>();
            wrapper.eq("dict_code",dictCode);
            Dict codedict = baseMapper.selectOne(wrapper);
            Long parent_id = codedict.getId();
            //根据parent_id和value值进行查询
            Dict dict = baseMapper.selectOne(new QueryWrapper<Dict>()
                    .eq("parent_id", parent_id)
                    .eq("value", value));
            return dict.getName();
        }

    }

    /**
     * 根据dictCode查询下级
     * @param dictCode
     * @return
     */
    @Override
    public List<Dict> findByDictCode(String dictCode) {
        //根据dictcode获取对应id
        QueryWrapper<Dict> wrapper=new QueryWrapper<>();
        wrapper.eq("dict_code",dictCode);
        Dict codedict = baseMapper.selectOne(wrapper);
        Long parent_id = codedict.getId();
        //根据id获取子节点
        List<Dict> chlidData = this.findChlidData(parent_id);
        return chlidData;
    }


    /**
     * 判断id下面是否有子节点
     * @param id
     * @return
     */
    private  boolean hasChildren(Long id){
        QueryWrapper<Dict> wrapper=new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        Integer count = baseMapper.selectCount(wrapper);
        return count > 0;
    }
}
