package com.sysml.lightmodel.service;

import com.sysml.lightmodel.semantic.Element;

import java.util.List;

public interface SemanticElementService {

    Element createElement(Element element);

    Element updateElement(Element element);

    boolean deleteElement(Long id);

    Element getElementById(Long id);

    List<Element> getAllElements();

    List<Element> getElementTree(); // 返回完整结构树

    List<Element> getElementsByType(String type);
    List<Element> getReferenceableTypes();

}

