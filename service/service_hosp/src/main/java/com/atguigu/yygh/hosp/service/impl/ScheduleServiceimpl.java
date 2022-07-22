package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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
    //带条件带分页查询科室
    @Override
    public Page selectPage(int page, int limit, ScheduleQueryVo scheduleQueryVo) {
        //1创建分页查询对象
        //1.1创建排序对象
        Sort sort = Sort.by(Sort.Direction.DESC,"createTime");
        //1.2创建分页对象（第一页为0）
        Pageable pageable = PageRequest.of(page-1,limit,sort);
        //2创建条件查询模板
        //2.1设置筛选条件
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo,schedule);
        //2.2设置模板构造器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        //2.3创建条件查询模板
        Example<Schedule> example = Example.of(schedule,matcher);
        //3进行带条件带分页查询
        Page<Schedule> pageModel = scheduleRepository.findAll(example, pageable);
        return pageModel;
    }
    //删除
    @Override
    public void removeSchedule(String hoscode, String hosScheduleId) {
        //1根据hoscode、depcode查询科室信息
        Schedule schedule =scheduleRepository.getByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        //2判断信息
        if(schedule==null){
            throw new YyghException(20001,"编码有误");
        }
        //3根据主键id删除
        scheduleRepository.deleteById(schedule.getId());
    }
}
