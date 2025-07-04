package com.sysml.lightmodel.service.impl;

import com.sysml.lightmodel.semantic.TypeLibraryElement;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于统一管理多个类型库（注册库 / 当前使用库）
 */
public class TypeLibraryRegistry {

    // libId -> 类型库定义列表
    private final Map<String, List<TypeLibraryElement>> libraries = new ConcurrentHashMap<>();

    // 当前使用库 ID
    private String currentLibraryId = "default";

    public void register(String libId, List<TypeLibraryElement> elements) {
        libraries.put(libId, elements);
    }

    public List<TypeLibraryElement> getCurrentLibrary() {
        return libraries.getOrDefault(currentLibraryId, Collections.emptyList());
    }

    public List<TypeLibraryElement> getLibrary(String libId) {
        return libraries.getOrDefault(libId, Collections.emptyList());
    }

    public void setCurrentLibraryId(String libId) {
        if (libraries.containsKey(libId)) {
            currentLibraryId = libId;
        }
    }

    public String getCurrentLibraryId() {
        return currentLibraryId;
    }

    public Set<String> listRegisteredLibraryIds() {
        return libraries.keySet();
    }
}
