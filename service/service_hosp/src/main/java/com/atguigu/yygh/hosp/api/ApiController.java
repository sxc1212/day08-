package com.atguigu.yygh.hosp.api;


import com.atguigu.yygh.common.Result;
import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.hosp.utils.HttpRequestHelper;
import com.atguigu.yygh.hosp.utils.MD5;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "医院管理API接口")
@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private HospitalSetService hospitalSetService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation(value = "上传医院")
    @PostMapping("saveHospital")
    public Result saveHospital(HttpServletRequest request) {
        //1获取参数，转化类型
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //2 校验签名
        //2.1从paramMap获取医院签名
        String sign = (String) paramMap.get("sign");
        String hoscode = (String) paramMap.get("hoscode");
        //2.2调用接口获取尚医通医院签名
        String targetSign = hospitalSetService.getSignKey(hoscode);
        //2.3签名md5加密
        String targetSignMD5 = MD5.encrypt(targetSign);
        System.out.println("sign = " + sign);
        System.out.println("targetSignMD5 = " + targetSignMD5);
        //2.4校验签名
        if (!targetSignMD5.equals(sign)) {
            throw new YyghException(20001, "校验签名失败");
        }
        //传输过程中“+”转换为了“ ”，因此我们要转换回来
        String logoData = (String) paramMap.get("logoData");
        logoData = logoData.replaceAll(" ", "+");
        paramMap.put("logoData", logoData);

        //3调用接口数据入库
        hospitalService.saveHospital(paramMap);
        return Result.ok();
    }
    @ApiOperation(value = "查询医院")
    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest request){
        //1获取参数，转化类型
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //2签名校验 省略
        String sign = (String)paramMap.get("sign");
        String hoscode = (String)paramMap.get("hoscode");
        //3获取参数，校验
        if(hoscode==null){
            throw  new YyghException(20001,"医院编码有误");
        }
        //4调用接口获取数据
        Hospital hospital = hospitalService.getHospital(hoscode);
        return Result.ok(hospital);
    }
    @ApiOperation(value = "上传科室")
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        //1获取参数，转化类型
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //2签名校验 省略
        //3调用接口数据入库
        departmentService.saveDepartment(paramMap);
        return Result.ok();
    }
    @ApiOperation(value = "带条件带分页查询科室")
    @PostMapping("department/list")
    public Result getDepartment(HttpServletRequest request){
        //1获取参数，转化类型
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //2获取参数
        String sign = (String)paramMap.get("sign");
        String hoscode = (String)paramMap.get("hoscode");
        //3签名校验 省略
        //4封装参数
        int page = StringUtils.isEmpty(paramMap.get("page"))?1:
                Integer.parseInt((String)paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit"))?10:
                Integer.parseInt((String)paramMap.get("limit"));
        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        //5调用接口带条件带分页查询科室
        Page pageModel = departmentService
                .selectPage(page,limit,departmentQueryVo);
        return Result.ok(pageModel);
    }
    @ApiOperation(value = "删除科室")
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request) {
        //1获取参数，转化类型
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //2获取参数
        String sign = (String)paramMap.get("sign");
        String hoscode = (String)paramMap.get("hoscode");
        String depcode = (String)paramMap.get("depcode");
        //3签名校验 省略
        //4调用接口删除数据
        departmentService.removeDepartment(hoscode,depcode);
        return Result.ok();
    }
    @ApiOperation(value = "上传排班")
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request) {
        //1获取参数，转化类型
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //2签名校验 省略
        //3调用接口数据入库
        scheduleService.saveSchedule(paramMap);
        return Result.ok();
    }
    @ApiOperation(value = "带条件带分页查询")
    @PostMapping("schedule/list")
    public Result getSchedule(HttpServletRequest request){
        //1获取参数，转化类型
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //2获取参数
        String sign = (String)paramMap.get("sign");
        String hoscode = (String)paramMap.get("hoscode");
        //3签名校验 省略
        //4封装参数
        int page = StringUtils.isEmpty(paramMap.get("page"))?1:
                Integer.parseInt((String)paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit"))?10:
                Integer.parseInt((String)paramMap.get("limit"));
        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        //5调用接口带条件带分页查询科室
        Page pageModel = scheduleService.selectPage(page,limit,scheduleQueryVo);
        return Result.ok(pageModel);
    }
    @ApiOperation(value = "删除")
    @PostMapping("schedule/remove")
    public Result removeSchedule(HttpServletRequest request) {
        //1获取参数，转化类型
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);
        //2获取参数
        String sign = (String)paramMap.get("sign");
        String hoscode = (String)paramMap.get("hoscode");
        String hosScheduleId = (String)paramMap.get("hosScheduleId");
        //3签名校验 省略
        //4调用接口删除数据
        scheduleService.removeSchedule(hoscode,hosScheduleId);
        return Result.ok();
    }
}

