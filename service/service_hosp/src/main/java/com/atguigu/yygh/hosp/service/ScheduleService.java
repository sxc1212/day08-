package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ScheduleService {
    void saveSchedule(Map<String, Object> paramMap);

    Page selectPage(int page, int limit, ScheduleQueryVo scheduleQueryVo);

    void removeSchedule(String hoscode, String depcode);
}
