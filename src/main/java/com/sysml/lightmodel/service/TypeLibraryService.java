package com.sysml.lightmodel.service;


import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.semantic.TypeLibraryElement;

import java.util.List;

public interface TypeLibraryService {

    List<TypeLibraryElement> getAllTypeDefinitions();

    List<TypeLibraryElement> getAllTypeDefinitions(String libId);

    TypeLibraryElement getTypeDefinitionById(String id);

    void importDefinitions(List<TypeLibraryElement> list);

    List<Element> cloneElementsFromType(String id);

    List<TypeLibraryElement> getStructureDefinitions();

    String getCurrentLibraryId();

    void switchLibrary(String libId);
}
