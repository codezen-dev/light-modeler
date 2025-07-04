package com.sysml.lightmodel.semantic;

import lombok.Data;

import java.util.List;

/**
 * 用于定义类型库中的一个模型结构，类似 StructureDefinition
 */
@Data
public class TypeLibraryElement {
    private String id;                 // 用于唯一标识
    private String name;               // 类型名，如 Block1
    private String type;               // 实际类型，一般为 StructureDefinition
    private String documentation;      // 描述
    private List<Element> children;    // 内部结构定义，兼容 Element.children
}
