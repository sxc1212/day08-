package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface DepartmentService {
    void saveDepartment(Map<String, Object> paramMap);

    Page selectPage(int page, int limit, DepartmentQueryVo departmentQueryVo);

    void removeDepartment(String hoscode, String depcode);
}
