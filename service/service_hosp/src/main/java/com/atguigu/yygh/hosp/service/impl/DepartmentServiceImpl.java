package com.atguigu.yygh.hosp.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void saveDepartment(Map<String, Object> paramMap) {
        //1转化参数类型
        String paramJsonString = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(paramJsonString, Department.class);
        //2查询科室信息（hoscode、 depcode）
        Department targetDepartment =
                departmentRepository.getByHoscodeAndDepcode(
                        department.getHoscode(),department.getDepcode());
        if(targetDepartment!=null){
            //3存在，更新
            department.setId(targetDepartment.getId());
            department.setCreateTime(targetDepartment.getCreateTime());
            department.setUpdateTime(new Date());
            department.setIsDeleted(targetDepartment.getIsDeleted());
            departmentRepository.save(department);
        }else {
            //4不存在，新增
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    //带条件带分页查询科室
    @Override
    public Page selectPage(int page, int limit, DepartmentQueryVo departmentQueryVo) {
        //1创建分页查询对象
        //1.1创建排序对象
        Sort sort = Sort.by(Sort.Direction.DESC,"createTime");
        //1.2创建分页对象（第一页为0）
        Pageable pageable = PageRequest.of(page-1,limit,sort);
        //2创建条件查询模板
        //2.1设置筛选条件
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo,department);
        //2.2设置模板构造器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        //2.3创建条件查询模板
        Example<Department> example = Example.of(department,matcher);
        //3进行带条件带分页查询
        Page<Department> pageModel = departmentRepository.findAll(example, pageable);
        return pageModel;
    }
    //删除科室
    @Override
    public void removeDepartment(String hoscode, String depcode) {
        //1根据hoscode、depcode查询科室信息
        Department department =
                departmentRepository.getByHoscodeAndDepcode(hoscode, depcode);
        //2判断科室信息
        if(department==null){
            throw new YyghException(20001,"科室编码有误");
        }
        //3根据主键id删除
        departmentRepository.deleteById(department.getId());

    }

    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        //1创建返回集合对象
        List<DepartmentVo> departmentVoList = new ArrayList<>();
        //2根据hoscode查询所有科室信息
        List<Department> departmentList = departmentRepository.getByHoscode(hoscode);
        //3实现根据bigcode进行分组，
        // List<Department> =>map k: bigcode  v:List<Department>
        Map<String,List<Department>> depListMap =
                departmentList.stream().collect(
                        Collectors.groupingBy(Department::getBigcode));

        //4遍历封装大科室信息
        for (Map.Entry<String, List<Department>> entry : depListMap.entrySet()) {
            //4.1创建大科室对象
            DepartmentVo bigDepVo = new DepartmentVo();
            //4.2封装大科室信息
            bigDepVo.setDepcode(entry.getKey());
            bigDepVo.setDepname(entry.getValue().get(0).getBigname());

            //5封装有关联的小科室信息
            //5.1创建封装小科室信息集合
            List<DepartmentVo> depVoList = new ArrayList<>();
            List<Department> depList = entry.getValue();
            //5.2遍历depList进行封装
            for (Department department : depList) {
                DepartmentVo depVo = new DepartmentVo();
                depVo.setDepcode(department.getDepcode());
                depVo.setDepname(department.getDepname());
                depVoList.add(depVo);
            }
            //6把小科室集合存入大科室对象
            bigDepVo.setChildren(depVoList);
            //7把大科室对象存入最终返回集合
            departmentVoList.add(bigDepVo);
        }
        return departmentVoList;
    }

    //根据科室编号，和医院编号，查询科室名称
    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if(department != null) {
            return department.getDepname();
        }
        return null;
    }

}



