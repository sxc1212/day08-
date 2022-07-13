package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.R;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(description = "医院设置接口")
@CrossOrigin
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {
    @Autowired
    private HospitalSetService hospitalSetService;

    @ApiOperation(value = "医院设置列表")
    @GetMapping("findAll")
    public R findAll() {
        List<HospitalSet> list = hospitalSetService.list();
        return R.ok().data("list",list);
    }

    @ApiOperation(value = "医院设置删除")
    @DeleteMapping("{id}")
    public R removeById(@ApiParam(name = "id", value = "讲师ID", required = true) @PathVariable String id){
        hospitalSetService.removeById(id);
        return R.ok();
    }
}

