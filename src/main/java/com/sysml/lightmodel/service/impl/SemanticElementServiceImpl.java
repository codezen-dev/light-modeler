package com.sysml.lightmodel.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sysml.lightmodel.mapper.ElementMapper;
import com.sysml.lightmodel.semantic.Definition;
import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.service.SemanticElementService;
import com.sysml.lightmodel.utils.IdGenerator;
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
        if (element.getId() == null) {
            element.setId(IdGenerator.nextId());
        }

        if (element.getType() == null) {
            element.setType(element.getClass().getSimpleName());
        }

        elementMapper.insert(element);

        // ✅ 关键：如果是 Definition 类型，则递归 ownedUsages
        if (element instanceof Definition def && def.getOwnedUsages() != null) {
            for (Element usage : def.getOwnedUsages()) {
                usage.setOwner(String.valueOf(element.getId()));
                createElement(usage);
            }
        }

        // ✅ 兼容处理 children 字段
        if (element.getChildren() != null) {
            for (Element child : element.getChildren()) {
                child.setOwner(String.valueOf(element.getId()));
                createElement(child);
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
    @Transactional
    public boolean deleteElement(Long id) {
        // 递归删除所有子元素
        deleteChildren(id);
        return elementMapper.deleteById(id) > 0;
    }

    private void deleteChildren(Long parentId) {
        List<Element> children = getElementsByOwner(parentId.toString());
        for (Element child : children) {
            deleteChildren(child.getId()); // 递归删除孙子
            elementMapper.deleteById(child.getId());
        }
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
        Map<Long, Element> idMap = all.stream()
                .filter(e -> e.getId() != null)
                .collect(Collectors.toMap(Element::getId, e -> e));

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
                } catch (NumberFormatException ignore) {
                    // owner 字段不是有效数字，忽略
                }
            }
        }

        // ✅ 判断 definition/type 是否为已知元素
        for (Element element : all) {
            Map<String, Object> meta = element.getMetadata();
            if (meta == null) continue;

            if (meta.containsKey("definition")) {
                try {
                    Long defId = Long.valueOf(meta.get("definition").toString());
                    if (!idMap.containsKey(defId)) {
                        meta.put("definitionUnresolved", true);
                    }
                } catch (NumberFormatException ignore) {}
            }

            if (meta.containsKey("type")) {
                try {
                    Long typeId = Long.valueOf(meta.get("type").toString());
                    if (!idMap.containsKey(typeId)) {
                        meta.put("typeUnresolved", true);
                    }
                } catch (NumberFormatException ignore) {}
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

    @Override
    public List<Element> getElementsByOwner(String owner) {
        QueryWrapper<Element> query = new QueryWrapper<>();
        if (owner == null) {
            query.isNull("owner");
        } else {
            query.eq("owner", owner);
        }
        return elementMapper.selectList(query);
    }

    @Override
    public Element getElementTree(Long id) {
        Element root = elementMapper.selectById(id);
        if (root == null) return null;

        // 查询所有元素用于构建子树
        List<Element> all = elementMapper.selectList(null);

        // 构建 id -> children 映射
        Map<Long, List<Element>> childrenMap = new HashMap<>();
        for (Element e : all) {
            if (e.getOwner() != null) {
                childrenMap
                        .computeIfAbsent(Long.valueOf(e.getOwner()), k -> new ArrayList<>())
                        .add(e);
            }
        }

        // 递归构建子节点
        buildChildren(root, childrenMap);

        return root;
    }



    private void buildChildren(Element parent, Map<Long, List<Element>> childrenMap) {
        List<Element> children = childrenMap.get(parent.getId());
        if (children != null) {
            parent.setChildren(children);
            for (Element child : children) {
                buildChildren(child, childrenMap);
            }
        } else {
            parent.setChildren(new ArrayList<>()); // 保证非 null
        }
    }


}
