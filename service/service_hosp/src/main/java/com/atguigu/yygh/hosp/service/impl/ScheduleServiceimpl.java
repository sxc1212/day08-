package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class ScheduleServiceimpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;
    //上传排班
    @Override
    public void saveSchedule(Map<String, Object> paramMap) {
        //1转化参数类型
        String paramJsonString = JSONObject.toJSONString(paramMap);
        Schedule schedule = JSONObject.parseObject(paramJsonString, Schedule.class);
        //2查询排班信息（hoscode，hosScheduleId）
        Schedule targetSchedule =
                scheduleRepository.getByHoscodeAndHosScheduleId(
                        schedule.getHoscode(),schedule.getHosScheduleId()
                );
        if(targetSchedule!=null){
            //3存在，更新
            schedule.setId(targetSchedule.getId());
            schedule.setCreateTime(targetSchedule.getCreateTime());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        }else{
            //4不存在，新增
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        }
    }

}
