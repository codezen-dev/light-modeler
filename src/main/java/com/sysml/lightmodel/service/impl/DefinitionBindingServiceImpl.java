package com.sysml.lightmodel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sysml.lightmodel.mapper.ElementMapper;
import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.service.DefinitionBindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefinitionBindingServiceImpl implements DefinitionBindingService {

    @Autowired
    private ElementMapper elementMapper;

    @Override
    public Element bind(String typeName, String expectedType) {
        QueryWrapper<Element> query = new QueryWrapper<>();
        query.eq("name", typeName).eq("type", expectedType);

        Element def = elementMapper.selectOne(query);
        if (def == null) {
            def = new Element();
            def.setName(typeName);
            def.setType(expectedType);
            elementMapper.insert(def);
        }
        return def;
    }
}
