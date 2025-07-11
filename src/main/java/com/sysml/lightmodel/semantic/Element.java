
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

    private String type;

    private String definitionName;

    private Long definitionId;

    @TableField(typeHandler = ListToJsonTypeHandler.class)
    private List<String> modifiers;

    @TableField(typeHandler = JsonObjectHandler.class)
    private Map<String, Object> metadata;

    @TableField(exist = false)
    private List<Element> children = new ArrayList<>();

    private String documentation;

    @TableField(exist = false)
    private Element ownerElement;

    @TableField(exist = false)
    private Element resolvedDefinition; // 运行时解析的引用
}

