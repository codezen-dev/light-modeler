package com.sysml.lightmodel.dsl.parser;

import lombok.Data;

@Data
public class DslRawEntry {
    public String type;     // 如 StructureDefinition
    public String name;     // 名称
    public String header;   // 整行首部
    public String body;     // 花括号内原始内容
}

