package com.sysml.lightmodel.service;

import com.sysml.lightmodel.semantic.Element;

import java.util.List;

public interface DslImportService {

    /**
     * 将 DSL 文本解析为语义模型结构
     * @param dslText DSL 格式文本
     * @return Element 列表（一般是 Definition 类型为根）
     */
    List<Element> parseDsl(String dslText);
}
