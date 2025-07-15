package com.sysml.lightmodel.service.impl;

import com.sysml.lightmodel.dsl.parser.DslParserRegistry;
import com.sysml.lightmodel.dsl.parser.DslRawEntry;
import com.sysml.lightmodel.dsl.parser.DslTokenizer;
import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.service.DslImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultDslImportService implements DslImportService {

    private final DslParserRegistry dslParserRegistry;

    @Override
    public List<Element> parseDsl(String dslText) {
        List<DslRawEntry> entries = DslTokenizer.tokenize(dslText);
        List<Element> elements = new ArrayList<>();
        for (DslRawEntry entry : entries) {
            elements.add(dslParserRegistry.getParser(entry.type).parse(entry));
        }
        return elements;
    }
}

