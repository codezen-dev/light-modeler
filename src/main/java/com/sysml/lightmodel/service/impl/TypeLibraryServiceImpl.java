package com.sysml.lightmodel.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.semantic.TypeLibraryElement;
import com.sysml.lightmodel.service.TypeLibraryService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 简单内存型类型库服务，后续可持久化
 */
@Service
public class TypeLibraryServiceImpl implements TypeLibraryService {

    private final TypeLibraryRegistry registry = new TypeLibraryRegistry();

    @PostConstruct
    public void loadBuiltin() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("type-library/default.json")) {
            if (is != null) {
                List<TypeLibraryElement> builtins = new ObjectMapper()
                        .readValue(is, new TypeReference<>() {
                        });
                registry.register("default", builtins);
                registry.setCurrentLibraryId("default");
            }
        } catch (Exception e) {
            System.err.println("无法加载默认类型库: " + e.getMessage());
        }
    }

    @Override
    public List<TypeLibraryElement> getAllTypeDefinitions() {
        return registry.getCurrentLibrary();
    }

    @Override
    public TypeLibraryElement getTypeDefinitionById(String id) {
        return registry.getCurrentLibrary().stream()
                .filter(e -> Objects.equals(e.getId(), id))
                .findFirst().orElse(null);
    }

    @Override
    public void importDefinitions(List<TypeLibraryElement> list) {
        registry.register("custom-" + System.currentTimeMillis(), list);
    }

    public void importDefinitions(String libId, List<TypeLibraryElement> list) {
        registry.register(libId, list);
    }

    @Override
    public List<Element> cloneElementsFromType(String id) {
        TypeLibraryElement source = getTypeDefinitionById(id);
        if (source == null) return Collections.emptyList();
        return source.getChildren().stream()
                .map(this::deepClone)
                .collect(Collectors.toList());
    }

    public void switchLibrary(String libId) {
        registry.setCurrentLibraryId(libId);
    }

    public String getCurrentLibraryId() {
        return registry.getCurrentLibraryId();
    }

    public List<TypeLibraryElement> getStructureDefinitions() {
        return registry.getCurrentLibrary().stream()
                .filter(e -> "StructureDefinition".equals(e.getType()))
                .collect(Collectors.toList());
    }

    public Set<String> getRegisteredLibraryIds() {
        return registry.listRegisteredLibraryIds();
    }

    private Element deepClone(Element source) {
        Element target = new Element();
        target.setName(source.getName());
        target.setType(source.getType());
        target.setDocumentation(source.getDocumentation());
        target.setOwner(null); // 插入当前模型时设置
        target.setModifiers(source.getModifiers() != null ? new ArrayList<>(source.getModifiers()) : null);
        target.setMetadata(source.getMetadata() != null ? new HashMap<>(source.getMetadata()) : null);
        if (source.getChildren() != null) {
            target.setChildren(
                    source.getChildren().stream().map(this::deepClone).collect(Collectors.toList())
            );
        }
        return target;
    }

}
