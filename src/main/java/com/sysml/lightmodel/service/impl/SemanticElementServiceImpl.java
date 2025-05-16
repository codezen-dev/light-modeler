package com.sysml.lightmodel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sysml.lightmodel.mapper.ElementMapper;
import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.service.SemanticElementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SemanticElementServiceImpl implements SemanticElementService {

    @Autowired
    private ElementMapper elementMapper;

    @Override
    @Transactional
    public Element createElement(Element element) {
        if (element.getType() == null) {
            element.setType(element.getClass().getSimpleName());
        }

        // 保存当前元素
        elementMapper.insert(element);

        // 保存子元素
        if (element.getChildren() != null) {
            for (Element child : element.getChildren()) {
                child.setOwner(String.valueOf(element.getId())); // 设置父 ID
                createElement(child); // 递归保存
            }
        }

        return element;
    }


    @Override
    public Element updateElement(Element element) {
        elementMapper.updateById(element);
        return element;
    }

    @Override
    public boolean deleteElement(Long id) {
        return elementMapper.deleteById(id) > 0;
    }

    @Override
    public Element getElementById(Long id) {
        return elementMapper.selectById(id);
    }

    @Override
    public List<Element> getAllElements() {
        return elementMapper.selectList(null);
    }

    @Override
    public List<Element> getElementTree() {
        List<Element> all = elementMapper.selectList(null);
        Map<Long, Element> idMap = all.stream().collect(Collectors.toMap(Element::getId, e -> e));

        List<Element> roots = new ArrayList<>();
        for (Element element : all) {
            if (element.getOwner() == null) {
                roots.add(element);
            } else {
                try {
                    Long parentId = Long.valueOf(element.getOwner());
                    Element parent = idMap.get(parentId);
                    if (parent != null) {
                        parent.getChildren().add(element);
                    }
                } catch (NumberFormatException e) {
                    // 忽略 owner 字段不为数字的情况
                }
            }
        }
        return roots;
    }

    @Override
    public List<Element> getElementsByType(String type) {
        QueryWrapper<Element> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type);
        return elementMapper.selectList(queryWrapper);
    }

    @Override
    public List<Element> getReferenceableTypes() {
        QueryWrapper<Element> query = new QueryWrapper<>();
        query.in("type", List.of("StructureDefinition", "ValueDefinition"));
        return elementMapper.selectList(query);
    }

}
