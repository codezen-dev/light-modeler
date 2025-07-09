package com.sysml.lightmodel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.sysml.lightmodel.mapper.DslDocumentMapper;
import com.sysml.lightmodel.pojo.DslDocument;
import com.sysml.lightmodel.service.DslDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DslDocumentServiceImpl implements DslDocumentService {

    private final DslDocumentMapper mapper;

    @Override
    public DslDocument findByRootId(Long rootElementId) {
        // 使用 JSON 查询包含此 ID 的文档
        // 注意：element_ids 是 JSON 存储格式（如 [123,456,...]）
        LambdaQueryWrapper<DslDocument> query = new LambdaQueryWrapper<>();
        query.apply("JSON_CONTAINS(element_ids, CAST({0} AS JSON))", rootElementId);
        List<DslDocument> docs = mapper.selectList(query);
        return docs.isEmpty() ? null : docs.get(0); // 目前一对一绑定
    }


    @Override
    public DslDocument saveDsl(String name, String content, List<Long> elementIds) {
        DslDocument doc = new DslDocument();
        doc.setId(IdWorker.getId());
        doc.setName(name);
        doc.setContent(content);
        doc.setElementIds(elementIds);
        doc.setCreatedAt(LocalDateTime.now());
        mapper.insert(doc);
        return doc;
    }

    @Override
    public List<DslDocument> getAll() {
        return mapper.selectList(null);
    }

    @Override
    public DslDocument getById(Long id) {
        return mapper.selectById(id);
    }
}

