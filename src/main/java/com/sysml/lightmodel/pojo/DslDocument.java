package com.sysml.lightmodel.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sysml.lightmodel.handler.ListToJsonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("dsl_document")
public class DslDocument {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String name; // 可选：文件名、模块名等

    private String content; // 原始 DSL 文本

    @TableField(typeHandler = ListToJsonTypeHandler.class)
    private List<Long> elementIds; // 解析后的元素 ID 列表（数据库中已有）

    private LocalDateTime createdAt;
}

