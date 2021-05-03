package com.zcj.yygh.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcj.yygh.model.cmn.Dict;
import com.zcj.yygh.model.hosp.HospitalSet;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author 朱长江
 * @date 2021/4/15
 */

public interface DictService extends IService<Dict> {
    List<Dict> findChlidData(Long id);

    void exportDictData(HttpServletResponse response);

    void importDictData(MultipartFile file);

    String getDictName(String dictCode, String value);

    List<Dict> findByDictCode(String dictCode);
}
