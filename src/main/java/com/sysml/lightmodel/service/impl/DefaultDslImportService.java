package com.sysml.lightmodel.service.impl;

import com.sysml.lightmodel.semantic.*;
import com.sysml.lightmodel.service.DslDocumentService;
import com.sysml.lightmodel.service.DslImportService;
import com.sysml.lightmodel.service.SemanticElementService;
import com.sysml.lightmodel.service.TypeLibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultDslImportService implements DslImportService {

    private final TypeLibraryService typeLibraryService;
    private final SemanticElementService elementService;
    private final DslDocumentService dslDocumentService;

    private static final Set<String> KNOWN_USAGE_KEYWORDS = Set.of("part", "attr", "attribute", "constraint", "action");

    @Override
    public List<Element> parseDsl(String dslText) {
        List<Element> allElements = new ArrayList<>();

        if (dslText == null || dslText.isBlank()) {
            return allElements;
        }

        DslParser parser = new DslParser(dslText);
        ParsedDocument document = parser.parse();

        loadImportedLibraries(document.getImports(), allElements);

        for (ParsedDefinition definition : document.getDefinitions()) {
            buildDefinition(definition, null, allElements);
        }

        return allElements;
    }

    @Override
    @Transactional
    public List<Element> importDslWithPersistence(String dslText) {
        List<Element> elements = parseDsl(dslText);
        DefinitionBinder.bindAll(elements);

        // ✅ 数据库中已有的元素
        List<Element> existingDbElements = elementService.getAllElements();
        Map<String, Element> existingMap = existingDbElements.stream()
                .filter(e -> e.getName() != null && e.getType() != null)
                .collect(Collectors.toMap(e -> e.getName() + ":" + e.getType(), e -> e, (a, b) -> a));

        Set<String> existingKeys = new HashSet<>(existingMap.keySet());
        List<Element> toSave = new ArrayList<>();

        for (Element e : elements) {
            if (e instanceof Definition def) {
                String defKey = def.getName() + ":" + def.getType();
                if (!existingKeys.contains(defKey)) {
                    def.setId(null); // 🔒 清空 ID，避免主键冲突
                    toSave.add(def);
                    existingKeys.add(defKey);
                }

                if (def.getOwnedUsages() != null) {
                    for (Usage usage : def.getOwnedUsages()) {
                        usage.setId(null);
                        usage.setOwner(def.getId() == null ? null : def.getId().toString());
                        String usageKey = usage.getName() + ":" + usage.getType() + ":" + def.getName(); // 避免重名重复
                        if (!existingKeys.contains(usageKey)) {
                            toSave.add(usage);
                            existingKeys.add(usageKey);
                        }
                    }
                }
            } else {
                String key = e.getName() + ":" + e.getType();
                if (!existingKeys.contains(key)) {
                    e.setId(null);
                    toSave.add(e);
                    existingKeys.add(key);
                }
            }
        }

        // ✅ 自动绑定 orphan Usage 的 owner
        for (Element e : toSave) {
            if (e instanceof Usage u && u.getOwner() == null) {
                Definition parent = findParentDefinition(elements, u);
                if (parent != null) {
                    u.setOwner(parent.getId() == null ? null : parent.getId().toString());
                }
            }
        }

        // ✅ 执行插入
        List<Long> savedIds = new ArrayList<>();
        for (Element e : toSave) {
            Element saved = elementService.createElement(e);
            savedIds.add(saved.getId());
        }

        dslDocumentService.saveDsl("Unnamed", dslText, savedIds);
        return elements;
    }






    private void loadImportedLibraries(List<String> imports, List<Element> collector) {
        if (imports == null || imports.isEmpty()) {
            return;
        }
        Set<String> handled = new LinkedHashSet<>();
        for (String lib : imports) {
            String normalizedLib = "sys-types".equals(lib) ? "default" : lib;
            if (!handled.add(normalizedLib)) {
                continue;
            }
            List<TypeLibraryElement> typeDefs = typeLibraryService.getAllTypeDefinitions();
            for (TypeLibraryElement libType : typeDefs) {
                collector.add(toElement(libType));
            }
        }
    }

    private Definition buildDefinition(ParsedDefinition node, Element owner, List<Element> collector) {
        Definition definition = new Definition();
        definition.setType(node.getKind());
        definition.setName(node.getName());
        definition.setChildren(new ArrayList<>());
        List<Usage> ownedUsages = new ArrayList<>();
        definition.setOwnedUsages(ownedUsages);
        if (owner != null) {
            definition.setOwner(owner.getName());
        }

        collector.add(definition);

        for (ParsedNode child : node.getChildren()) {
            if (child instanceof ParsedUsage usageNode) {
                Usage usage = buildUsage(usageNode, definition, definition, ownedUsages, collector);
                definition.getChildren().add(usage);
            } else if (child instanceof ParsedDefinition nestedNode) {
                Definition nested = buildDefinition(nestedNode, definition, collector);
                definition.getChildren().add(nested);
            }
        }

        return definition;
    }

    private Usage buildUsage(ParsedUsage node, Definition owningDefinition, Element owner,
                             List<Usage> owningDefinitionCollector, List<Element> elementCollector) {
        Usage usage = new Usage();
        usage.setName(node.getName());
        usage.setType(resolveUsageType(node.getKeyword()));
        usage.setDefinitionName(node.getDefinitionName());
        usage.setMultiplicity(node.getMultiplicity());
        usage.setDefaultValue(node.getDefaultValue());
        usage.setParentDefinition(owningDefinition);
        if (owner != null) {
            usage.setOwner(owner.getName());
        }
        usage.setChildren(new ArrayList<>());

        owningDefinitionCollector.add(usage);

        for (ParsedNode child : node.getChildren()) {
            if (child instanceof ParsedUsage usageNode) {
                Usage nestedUsage = buildUsage(usageNode, owningDefinition, usage, owningDefinitionCollector, elementCollector);
                usage.getChildren().add(nestedUsage);
            } else if (child instanceof ParsedDefinition defNode) {
                Definition nestedDefinition = buildDefinition(defNode, usage, elementCollector);
                usage.getChildren().add(nestedDefinition);
            }
        }

        return usage;
    }

    private String resolveUsageType(String keyword) {
        String lower = keyword.toLowerCase(Locale.ROOT);
        return switch (lower) {
            case "attr", "attribute" -> "AttributeUsage";
            case "part" -> "PartUsage";
            case "constraint" -> "ConstraintUsage";
            case "action" -> "ActionUsage";
            default -> Character.toUpperCase(keyword.charAt(0)) + keyword.substring(1) + "Usage";
        };
    }

    private Element toElement(TypeLibraryElement src) {
        Element e = new Element();
        e.setName(src.getName());
        e.setType(src.getType());
        e.setDocumentation(src.getDocumentation());
        return e;
    }

    private Definition findParentDefinition(List<Element> elements, Usage usage) {
        for (Element e : elements) {
            if (e instanceof Definition def && def.getOwnedUsages() != null) {
                if (def.getOwnedUsages().contains(usage)) {
                    return def;
                }
            }
        }
        return null;
    }

    private static final class ParsedDocument {
        private final List<String> imports;
        private final List<ParsedDefinition> definitions;

        ParsedDocument(List<String> imports, List<ParsedDefinition> definitions) {
            this.imports = imports;
            this.definitions = definitions;
        }

        List<String> getImports() {
            return imports;
        }

        List<ParsedDefinition> getDefinitions() {
            return definitions;
        }
    }

    private abstract static class ParsedNode {
        private final List<ParsedNode> children;

        protected ParsedNode(List<ParsedNode> children) {
            this.children = children;
        }

        List<ParsedNode> getChildren() {
            return children;
        }
    }

    private static final class ParsedDefinition extends ParsedNode {
        private final String kind;
        private final String name;

        ParsedDefinition(String kind, String name, List<ParsedNode> children) {
            super(children);
            this.kind = kind;
            this.name = name;
        }

        String getKind() {
            return kind;
        }

        String getName() {
            return name;
        }
    }

    private static final class ParsedUsage extends ParsedNode {
        private final String keyword;
        private final String name;
        private final String definitionName;
        private final String multiplicity;
        private final String defaultValue;

        ParsedUsage(String keyword, String name, String definitionName, String multiplicity,
                    String defaultValue, List<ParsedNode> children) {
            super(children);
            this.keyword = keyword;
            this.name = name;
            this.definitionName = definitionName;
            this.multiplicity = multiplicity;
            this.defaultValue = defaultValue;
        }

        String getKeyword() {
            return keyword;
        }

        String getName() {
            return name;
        }

        String getDefinitionName() {
            return definitionName;
        }

        String getMultiplicity() {
            return multiplicity;
        }

        String getDefaultValue() {
            return defaultValue;
        }
    }

    private enum TokenType {
        WORD,
        STRING,
        SYMBOL,
        NEWLINE,
        EOF
    }

    private static final class Token {
        private final TokenType type;
        private final String text;
        private final int start;
        private final int end;

        Token(TokenType type, String text, int start, int end) {
            this.type = type;
            this.text = text;
            this.start = start;
            this.end = end;
        }

        TokenType getType() {
            return type;
        }

        String getText() {
            return text;
        }

        int getStart() {
            return start;
        }
    }

    private static final class Tokenizer {
        private final String source;
        private final int length;
        private final List<Token> tokens = new ArrayList<>();
        private int index;

        Tokenizer(String source) {
            this.source = source;
            this.length = source.length();
        }

        List<Token> tokenize() {
            while (index < length) {
                char c = source.charAt(index);

                if (c == '/' && index + 1 < length) {
                    char next = source.charAt(index + 1);
                    if (next == '/') {
                        index += 2;
                        while (index < length) {
                            char current = source.charAt(index);
                            if (current == '\n' || current == '\r') {
                                break;
                            }
                            index++;
                        }
                        continue;
                    } else if (next == '*') {
                        index += 2;
                        while (index + 1 < length && !(source.charAt(index) == '*' && source.charAt(index + 1) == '/')) {
                            index++;
                        }
                        if (index + 1 < length) {
                            index += 2;
                        }
                        continue;
                    }
                }

                if (c == '\r' || c == '\n') {
                    int start = index;
                    index++;
                    if (c == '\r' && index < length && source.charAt(index) == '\n') {
                        index++;
                    }
                    tokens.add(new Token(TokenType.NEWLINE, "\n", start, index));
                    continue;
                }

                if (Character.isWhitespace(c)) {
                    index++;
                    continue;
                }

                if (c == '"' || c == '\'') {
                    tokens.add(readString());
                    continue;
                }

                if (Character.isLetterOrDigit(c) || c == '_') {
                    tokens.add(readWord());
                    continue;
                }

                tokens.add(readSymbol());
            }

            tokens.add(new Token(TokenType.EOF, "", length, length));
            return tokens;
        }

        private Token readWord() {
            int start = index;
            index++;
            while (index < length) {
                char c = source.charAt(index);
                if (Character.isLetterOrDigit(c) || c == '_' || c == '-') {
                    index++;
                } else {
                    break;
                }
            }
            return new Token(TokenType.WORD, source.substring(start, index), start, index);
        }

        private Token readSymbol() {
            int start = index;
            index++;
            return new Token(TokenType.SYMBOL, source.substring(start, index), start, index);
        }

        private Token readString() {
            int start = index;
            char quote = source.charAt(index++);
            boolean escaped = false;
            while (index < length) {
                char c = source.charAt(index);
                index++;
                if (escaped) {
                    escaped = false;
                    continue;
                }
                if (c == '\\') {
                    escaped = true;
                    continue;
                }
                if (c == quote) {
                    break;
                }
            }
            return new Token(TokenType.STRING, source.substring(start, Math.min(index, length)), start, Math.min(index, length));
        }
    }

    private static final class DslParser {
        private final List<Token> tokens;
        private int index;

        DslParser(String source) {
            this.tokens = new Tokenizer(source).tokenize();
        }

        ParsedDocument parse() {
            List<String> imports = new ArrayList<>();
            List<ParsedDefinition> definitions = new ArrayList<>();

            while (!isEOF()) {
                skipNewlines();
                if (isEOF()) {
                    break;
                }
                Token token = peek();
                if (token.getType() == TokenType.WORD) {
                    String lower = token.getText().toLowerCase(Locale.ROOT);
                    if ("import".equals(lower)) {
                        consume();
                        String importName = parseImportArgument();
                        if (importName != null && !importName.isBlank()) {
                            imports.add(importName);
                        }
                        consumeUntilLineEnd();
                        continue;
                    }
                    if ("def".equals(lower)) {
                        definitions.add(parseDefinition());
                        continue;
                    }
                }
                skipUnknownTopLevel();
            }

            return new ParsedDocument(imports, definitions);
        }

        private ParsedDefinition parseDefinition() {
            consume();
            Token kindToken = expectWord("Expected definition kind");
            Token nameToken = expectWord("Expected definition name");

            skipNewlines();
            List<ParsedNode> children = new ArrayList<>();
            if (matchSymbol("{")) {
                children = parseBlock();
            }

            return new ParsedDefinition(kindToken.getText(), nameToken.getText(), children);
        }

        private ParsedUsage parseUsage() {
            Token keywordToken = consume();
            Token nameToken = expectWord("Expected usage name");

            skipNewlines();
            expectSymbol(":");
            skipNewlines();

            String definitionName = readDefinitionName();
            String multiplicity = null;
            if (matchSymbol("[")) {
                multiplicity = readMultiplicity();
            }
            String defaultValue = null;
            if (matchSymbol("=")) {
                defaultValue = readDefaultValue();
            }

            skipNewlines();
            List<ParsedNode> children = new ArrayList<>();
            if (matchSymbol("{")) {
                children = parseBlock();
            }

            return new ParsedUsage(keywordToken.getText(), nameToken.getText(), definitionName, multiplicity, defaultValue, children);
        }

        private List<ParsedNode> parseBlock() {
            List<ParsedNode> nodes = new ArrayList<>();
            while (!isEOF()) {
                skipNewlines();
                if (matchSymbol("}")) {
                    break;
                }
                Token token = peek();
                if (token.getType() == TokenType.WORD) {
                    String lower = token.getText().toLowerCase(Locale.ROOT);
                    if ("def".equals(lower)) {
                        nodes.add(parseDefinition());
                        continue;
                    }
                    if (DefaultDslImportService.KNOWN_USAGE_KEYWORDS.contains(lower)) {
                        nodes.add(parseUsage());
                        continue;
                    }
                }
                skipUnknownContent();
            }
            return nodes;
        }

        private String parseImportArgument() {
            skipNewlines();
            if (isEOF()) {
                return null;
            }
            Token token = peek();
            if (token.getType() == TokenType.STRING) {
                consume();
                return unquote(token.getText());
            }
            if (token.getType() == TokenType.WORD) {
                return consume().getText();
            }
            return null;
        }

        private String readDefinitionName() {
            StringBuilder builder = new StringBuilder();
            Token previous = null;
            while (!isEOF()) {
                Token token = peek();
                if (token.getType() == TokenType.NEWLINE) {
                    break;
                }
                if (token.getType() == TokenType.SYMBOL) {
                    String symbol = token.getText();
                    if ("[".equals(symbol) || "{".equals(symbol) || "}".equals(symbol) || "=".equals(symbol) || ";".equals(symbol)) {
                        break;
                    }
                }
                consume();
                appendToken(builder, token, previous);
                previous = token;
            }
            String result = builder.toString().trim();
            return result.isEmpty() ? null : result;
        }

        private String readMultiplicity() {
            StringBuilder builder = new StringBuilder();
            Token previous = null;
            while (!isEOF()) {
                Token token = peek();
                if (token.getType() == TokenType.SYMBOL && "]".equals(token.getText())) {
                    consume();
                    break;
                }
                if (token.getType() == TokenType.NEWLINE) {
                    consume();
                    break;
                }
                consume();
                appendToken(builder, token, previous);
                previous = token;
            }
            String result = builder.toString().trim();
            return result.isEmpty() ? null : result;
        }

        private String readDefaultValue() {
            StringBuilder builder = new StringBuilder();
            Token previous = null;
            while (!isEOF()) {
                Token token = peek();
                if (token.getType() == TokenType.NEWLINE) {
                    break;
                }
                if (token.getType() == TokenType.SYMBOL) {
                    String symbol = token.getText();
                    if ("{".equals(symbol) || "}".equals(symbol) || ";".equals(symbol)) {
                        break;
                    }
                }
                consume();
                appendToken(builder, token, previous);
                previous = token;
            }
            String result = builder.toString().trim();
            return result.isEmpty() ? null : result;
        }

        private void appendToken(StringBuilder builder, Token token, Token previous) {
            if (token.getType() == TokenType.SYMBOL) {
                builder.append(token.getText());
            } else {
                if (builder.length() > 0 && previous != null && previous.getType() != TokenType.SYMBOL) {
                    builder.append(' ');
                }
                builder.append(token.getText());
            }
        }

        private void skipNewlines() {
            while (!isEOF() && peek().getType() == TokenType.NEWLINE) {
                consume();
            }
        }

        private void consumeUntilLineEnd() {
            while (!isEOF()) {
                Token token = peek();
                consume();
                if (token.getType() == TokenType.NEWLINE) {
                    break;
                }
            }
        }

        private void skipUnknownTopLevel() {
            if (isEOF()) {
                return;
            }
            consume();
        }

        private void skipUnknownContent() {
            int depth = 0;
            while (!isEOF()) {
                Token token = peek();
                if (token.getType() == TokenType.SYMBOL) {
                    String symbol = token.getText();
                    if ("{".equals(symbol)) {
                        depth++;
                        consume();
                        continue;
                    }
                    if ("}".equals(symbol)) {
                        if (depth == 0) {
                            break;
                        }
                        depth--;
                        consume();
                        continue;
                    }
                }
                if (depth == 0 && token.getType() == TokenType.NEWLINE) {
                    consume();
                    break;
                }
                consume();
            }
        }

        private Token expectWord(String message) {
            Token token = peek();
            if (token.getType() == TokenType.WORD) {
                return consume();
            }
            throw new IllegalStateException(message + " at position " + token.getStart());
        }

        private void expectSymbol(String symbol) {
            if (!matchSymbol(symbol)) {
                Token token = peek();
                throw new IllegalStateException("Expected '" + symbol + "' at position " + token.getStart());
            }
        }

        private boolean matchSymbol(String symbol) {
            if (!isEOF()) {
                Token token = peek();
                if (token.getType() == TokenType.SYMBOL && token.getText().equals(symbol)) {
                    consume();
                    return true;
                }
            }
            return false;
        }

        private Token consume() {
            return tokens.get(index++);
        }

        private Token peek() {
            return tokens.get(index);
        }

        private boolean isEOF() {
            return index >= tokens.size() || tokens.get(index).getType() == TokenType.EOF;
        }

        private String unquote(String value) {
            if (value == null || value.length() < 2) {
                return value;
            }
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return value.substring(1, value.length() - 1)
                        .replace("\\\"", "\"")
                        .replace("\\'", "'");
            }
            return value;
        }
    }
}
