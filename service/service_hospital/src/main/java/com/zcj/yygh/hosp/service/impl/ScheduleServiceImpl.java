package com.zcj.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zcj.yygh.hosp.repository.ScheduleRepository;
import com.zcj.yygh.hosp.service.DepartmentService;
import com.zcj.yygh.hosp.service.HospitalService;
import com.zcj.yygh.hosp.service.ScheduleService;
import com.zcj.yygh.model.hosp.Department;
import com.zcj.yygh.model.hosp.Schedule;
import com.zcj.yygh.vo.hosp.BookingScheduleRuleVo;
import com.zcj.yygh.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 朱长江
 * @date 2021/4/20
 */

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    /**
     * 上传排班
     * @param requestMap
     */
    @Override
    public void save(Map<String, Object> requestMap) {
        //paramMap-->department对象
        String s = JSONObject.toJSONString(requestMap);
        Schedule schedule = JSONObject.parseObject(s, Schedule.class);
        //根据医院编号和科室编号查询
        Schedule scheduleExist = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(),schedule.getHosScheduleId());
        //判断
        if (scheduleExist!=null){
            scheduleExist.setUpdateTime(new Date());
            scheduleExist.setIsDeleted(0);
            scheduleExist.setStatus(1);
            scheduleRepository.save(scheduleExist);
        }else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }
    }

    /**
     *
     * 查询排班
     * @param page
     * @param limit
     * @param scheduleQueryVo
     * @return
     */
    @Override
    public Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo) {
        // 创建Pageable对象，设置当前页和每页记录数
        //0是第一页
        Pageable pageable = PageRequest.of(page-1,limit);
        // 创建Example对象
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo,schedule);
        schedule.setIsDeleted(0);
        schedule.setStatus(1);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Schedule> example = Example.of(schedule,matcher);

        return scheduleRepository.findAll(example, pageable);
    }

    /**
     * 删除排班
     * @param hoscode
     * @param hosScheduleId
     */
    @Override
    public void remove(String hoscode, String hosScheduleId) {
        Schedule schedule= scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if(schedule != null){
            scheduleRepository.deleteById(schedule.getId());
        }
    }

    /**
     * 根据医院编号和科室编号，查询排班规则数据
     * @param page 当前页
     * @param limit 每页数
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @return
     */
    @Override
    public Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode) {
        //1.根据医院编号和科室编号查询
        Criteria criteria=Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        //2.根据工作日期workDate进行分组
        Aggregation aggregation=Aggregation.newAggregation(
            Aggregation.match(criteria),
                //分组字段
            Aggregation.group("workDate").first("workDate").as("workDate")
             // 3.统计号源数量
            .count().as("docCount")
            .sum("reservedNumber").as("reservedNumber")
            .sum("availableNumber").as("availableNumber"),
             //排序
            Aggregation.sort(Sort.Direction.DESC,"workDate"),
            //4.实现分页
            Aggregation.skip((page-1)*limit),
            Aggregation.limit(limit)
        );
        AggregationResults<BookingScheduleRuleVo> aggResults= mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggResults.getMappedResults();

        //分组查询之后总的记录数
        Aggregation totalAgg=Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );

        AggregationResults<BookingScheduleRuleVo> totalAggResult= mongoTemplate.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);
        int  total=totalAggResult.getMappedResults().size();

        //把日期对应星期进行获取
        for (BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleVoList){
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        //设置最终的数据并进行返回
        Map<String,Object> result=new HashMap<>();
        result.put("bookingScheduleRuleList",bookingScheduleRuleVoList);
        result.put("total",total);
        //获取医院名称
        String hosName= hospitalService.getHospName(hoscode);
        Map<String,Object> baseMap=new HashMap<>();
        baseMap.put("hosname",hosName);
        result.put("baseMap",baseMap);
        return result;
    }

    /**
     * 根据医院编号、科室编号和工作日期，查询排班详细信息
     * @param hoscode
     * @param depcode
     * @param workDate
     * @return
     */
    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
       //根据参数擦好像Mongodb
        List<Schedule> scheduleList= scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,new DateTime(workDate).toDate());
       //把得到list集合遍历，设置其他值：医院名称、科室名称、日期对应星期
        scheduleList.stream().forEach(this::packageSchedule);
       return scheduleList;
    }

    /**
     * 封装排班详情其他值，医院名称、科室名称、日期对应星期
     * @param schedule
     */
    private void packageSchedule(Schedule schedule) {
        //设置医院名称
        schedule.getParam().put("hosname",hospitalService.getHospName(schedule.getHoscode()));
        //设置科室名称
        schedule.getParam().put("depname",departmentService.getDepName(schedule.getHoscode(),schedule.getDepcode()));
        //设置日期对应星期
        schedule.getParam().put("dayOfWeek",this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }

    /**
     * 根据日期获取周几数据
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }

}
