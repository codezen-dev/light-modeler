
package com.sysml.lightmodel.semantic;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sysml.lightmodel.handler.JsonObjectHandler;
import com.sysml.lightmodel.handler.ListToJsonTypeHandler;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base class of all model elements
 */
@Data
@TableName("element")
public class Element {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String name;

    private String owner;

    private String type; // 如：AttributeUsage、StructureDefinition 等

    @TableField(typeHandler = ListToJsonTypeHandler.class)
    private List<String> modifiers;

    @TableField(typeHandler = JsonObjectHandler.class)
    private Map<String, Object> metadata; // 用于扩展字段，如 direction、value、featureType 等

    @TableField(exist = false)
    private List<Element> children = new ArrayList<>();
}
