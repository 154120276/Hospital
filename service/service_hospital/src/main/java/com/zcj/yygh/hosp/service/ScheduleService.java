package com.zcj.yygh.hosp.service;

import com.zcj.yygh.model.hosp.Schedule;
import com.zcj.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author changjiang
 * @date 2016/10/31
 */

public interface ScheduleService {
    void save(Map<String, Object> requestMap);

    Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo);

    void remove(String hoscode, String hosScheduleId);

    Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode);

    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);
}
