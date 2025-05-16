package com.sysml.lightmodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sysml.lightmodel.semantic.Element;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ElementMapper extends BaseMapper<Element> {}
