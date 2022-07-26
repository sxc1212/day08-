package com.atguigu.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.cmn.listener.DictListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    @Autowired
    private DictListener dictListener;

    @Cacheable(value = "dict", key = "'selectIndexList'+#id")
    @Override
    public List<Dict> findChlidData(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        List<Dict> dictList = baseMapper.selectList(wrapper);
        //向list集合每个dict对象中设置hasChildren
        for (Dict dict : dictList) {
            Long dictId = dict.getId();
            boolean isChild = this.isChildren(dictId);
            dict.setHasChildren(isChild);
        }
        return dictList;
    }

    //判断id下面是否有子节点
    private boolean isChildren(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        Integer count = baseMapper.selectCount(wrapper);
        return count > 0;
    }

    @Override
    public void exportData(HttpServletResponse response) {
        try {
            //1设置response的基本参数
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("数据字典", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            //2查询所有字典数据
            List<Dict> dictList = baseMapper.selectList(null);
            //3转化字典数据类型
            List<DictEeVo> dictEeVoList = new ArrayList<>();
            for (Dict dict : dictList) {
                DictEeVo dictEeVo = new DictEeVo();
                BeanUtils.copyProperties(dict, dictEeVo);
                dictEeVoList.add(dictEeVo);
            }
            //4使用工具导出数据
            EasyExcel.write(response.getOutputStream(), DictEeVo.class)
                    .sheet("数据字典").doWrite(dictEeVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void importData(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            EasyExcel.read(inputStream, DictEeVo.class, dictListener)
                    .sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
            throw new YyghException(20001, "导入数据失败");
        }

    }

    @Override
    public String getNameByInfo(String parentDictCode, String value) {
        //1、判断parentDictCode是否为空
        if (StringUtils.isEmpty(parentDictCode)) {
            //2、国标数据查询
            Dict dict = baseMapper.selectOne(new QueryWrapper<Dict>()
                    .eq("value", value));
            if (dict != null) {
                return dict.getName();
            }
        } else {
            //3自定义数据查询
            Dict parentDict = this.getDictByDictCode(parentDictCode);
            Dict dict = baseMapper.selectOne(new QueryWrapper<Dict>()
                    .eq("parent_id", parentDict.getId())
                    .eq("value", value));
            if (dict != null) {
                return dict.getName();
            }
        }
        return "";

    }

    @Override
    public List<Dict> findByDictCode(String dictCode) {
        Dict dictByDictCode = this.getDictByDictCode(dictCode);
        List<Dict> dictList = this.findChlidData(dictByDictCode.getId());
        return dictList;
    }

    private Dict getDictByDictCode(String parentDictCode) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("dict_code", parentDictCode);
        Dict dict = baseMapper.selectOne(wrapper);
        return dict;
    }

}
