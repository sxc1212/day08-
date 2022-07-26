package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.BookingRule;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private DictFeignClient dictFeignClient;
    @Autowired
    private HospitalService hospitalService;

    @Override
    public void saveHospital(Map<String, Object> paramMap) {
        //1转化参数类型paramMap=>Hospital
        String paramJsonString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(paramJsonString, Hospital.class);
        //2根据hoscode查询医院信息
        Hospital targetHospital = hospitalRepository.getByHoscode(hospital.getHoscode());
        //3判断有医院信息进行更新
        if (targetHospital != null) {
            hospital.setId(targetHospital.getId());
            hospital.setCreateTime(targetHospital.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setStatus(targetHospital.getStatus());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        } else {
            //4没有医院信息进行新增
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setStatus(0);
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital getHospital(String hoscode) {
        Hospital hospital = hospitalRepository.getByHoscode(hoscode);
        return hospital;
    }

    @Override
    public Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        //1创建分页对像
        //1.1创建排序对象
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        //1.2创建分页对像
        Pageable pageable = PageRequest.of(page - 1, limit, sort);
        //2创建查询条件模板
        //2.1封装查询条件
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);
        //2.2创建模板构造器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        //2.3创建模板
        Example<Hospital> example = Example.of(hospital, matcher);
        //3实现条件分页查询
        Page<Hospital> pageModel = hospitalRepository.findAll(example, pageable);

        //4 跨模块（cmn）翻译字段
        pageModel.getContent().stream().forEach(item -> {
            this.packHospital(item);
        });

        return pageModel;

    }

    //更新上线状态
    @Override
    public void updateStatus(String id, Integer status) {
        if (status.intValue() == 0 || status.intValue() == 1) {
            //先查询
            Hospital hospital = hospitalRepository.findById(id).get();
            hospital.setStatus(status);
            hospital.setUpdateTime(new Date());
            hospitalRepository.save(hospital);
        }
    }

    //获取医院详情
    @Override
    public Map<String, Object> getHospitalById(String id) {
        //1根据id查询医院信息,翻译字段
        Hospital hospital = this.packHospital(hospitalRepository.findById(id).get()) ;
        //2取出预约规则
        BookingRule bookingRule = hospital.getBookingRule();
        hospital.setBookingRule(null);
        //3封装数据，返回
        Map<String, Object> map  = new HashMap<>();
        map.put("hospital",hospital);
        map.put("bookingRule",bookingRule);
        return map;
    }

    //获取医院名称
    @Override
    public String getHospName(String hoscode) {
        Hospital hospital = hospitalRepository.getByHoscode(hoscode);
        if(hospital==null){
            throw new YyghException(20001,"医院信息有误");
        }
        return hospital.getHosname();
    }


    private Hospital packHospital(Hospital hospital) {
        String hostypeString = dictFeignClient.getName(
                DictEnum.HOSTYPE.getDictCode(), hospital.getHostype());
        String provinceString = dictFeignClient.getName(hospital.getProvinceCode());
        String cityString = dictFeignClient.getName(hospital.getCityCode());
        String districtString = dictFeignClient.getName(hospital.getDistrictCode());

        hospital.getParam().put("hostypeString", hostypeString);
        hospital.getParam().put("fullAddress", provinceString + cityString + districtString + hospital.getAddress());
        return hospital;
    }
}
