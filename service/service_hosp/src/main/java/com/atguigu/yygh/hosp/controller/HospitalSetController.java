package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.GuliException;
import com.atguigu.yygh.common.R;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(description = "医院设置接口")
@CrossOrigin
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {
    @Autowired
    private HospitalSetService hospitalSetService;

    @ApiOperation(value = "模拟登陆")
    @PostMapping("login")
    public R login(){
        return R.ok().data("token","admin-token");
    }

    @ApiOperation(value = "模拟获取用户信息")
    @GetMapping("info")
    public R info(){
        Map<String,Object> map = new HashMap<>();
        map.put("roles","admin");
        map.put("introduction","I am a super administrator");
        map.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        map.put("name","Super Admin");
        return R.ok().data(map);
    }


    @ApiOperation(value = "医院设置列表")
    @GetMapping("findAll")
    public R findAll() {
//        try {
//            int a = 10/0;
//        }catch(Exception e) {
//            throw new GuliException(20001,"出现自定义异常");
//        }
        List<HospitalSet> list = hospitalSetService.list();
        return R.ok().data("list", list);
    }

    @ApiOperation(value = "医院设置删除")
    @DeleteMapping("{id}")
    public R removeById(@ApiParam(name = "id", value = "讲师ID", required = true) @PathVariable String id) {
        hospitalSetService.removeById(id);
        return R.ok();
    }

    @ApiOperation(value = "分页查询医院设置")
    @GetMapping("{page}/{limit}")
    public R pageList(@PathVariable Long page,
                      @PathVariable Long limit) {
        //1创建分页参数对象
        Page<HospitalSet> pageParam = new Page<>(page, limit);
        //2分页查询
        Page<HospitalSet> pageModel = hospitalSetService.page(pageParam);
        return R.ok().data("pageModel", pageModel);
    }

    @ApiOperation(value = "带分页带条件查询医院设置")
    @PostMapping("pageQuery/{page}/{limit}")
    public R pageList(@PathVariable Long page,
                      @PathVariable Long limit,
                      @RequestBody HospitalSetQueryVo hospitalSetQueryVo) {
        //1获取参数，验空，存入查询条件构造器
        String hosname = hospitalSetQueryVo.getHosname();
        String hoscode = hospitalSetQueryVo.getHoscode();
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(hosname)) {
            wrapper.like("hosname", hosname);
        }
        if (!StringUtils.isEmpty(hoscode)) {
            wrapper.eq("hoscode", hoscode);
        }
        //2创建分页参数对象
        Page<HospitalSet> pageParam = new Page<>(page, limit);
        //3分页查询
        Page<HospitalSet> pageModel =
                hospitalSetService.page(pageParam, wrapper);
        return R.ok().data("pageModel", pageModel);
    }

    @ApiOperation(value = "新增医院设置")
    @PostMapping("save")
    public R save(@RequestBody HospitalSet hospitalSet) {
        boolean save = hospitalSetService.save(hospitalSet);
        if (save) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @ApiOperation(value = "根据id查询医院设置")
    @GetMapping("getById/{id}")
    public R getById(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return R.ok().data("hospitalSet", hospitalSet);
    }

    @ApiOperation(value = "修改医院设置")
    @PostMapping("update")
    public R update(@RequestBody HospitalSet hospitalSet) {
        boolean update = hospitalSetService.updateById(hospitalSet);
        if (update) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @ApiOperation(value = "批量删除医院设置")
    @DeleteMapping("batchRemove")
    public R batchRemove(@RequestBody List<Long> idList) {
        boolean remove = hospitalSetService.removeByIds(idList);
        if (remove) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @PutMapping("lockHospitalSet/{id}/{status}")
    public R lockHospitalSet(@PathVariable Long id,
                             @PathVariable Integer status) {
        //1先查询
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        //2后更新
        hospitalSet.setStatus(status);
        boolean update = hospitalSetService.updateById(hospitalSet);
        if (update) {
            return R.ok();
        } else {
            return R.error();
        }
    }
}

