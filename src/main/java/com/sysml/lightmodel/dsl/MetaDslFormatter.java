package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.DefinitionResolver;
import com.sysml.lightmodel.semantic.DslRenderUtils;
import com.sysml.lightmodel.semantic.Element;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class MetaDslFormatter {

    public static String renderConstraintUsage(Element element, int indent) {
        String indentStr = DslRenderUtils.indent(indent);
        StringBuilder builder = new StringBuilder();

        builder.append(indentStr)
                .append("ConstraintUsage \"").append(element.getName()).append("\"");

        // 展示表达式（从 metadata 中取 expression 字符串）
        Map<String, Object> metadata = element.getMetadata();
        if (metadata != null && metadata.containsKey("expression")) {
            builder.append(" where ").append(metadata.get("expression"));
        }

        builder.append(" {\n");
        for (Element child : element.getChildren()) {
            builder.append(DslRendererRegistry.getRenderer(child.getType()).render(child, indent + 1));
        }
        builder.append(indentStr).append("}\n");

        return builder.toString();
    }


    public static String formatType(Map<String, Object> meta) {
        if (meta != null && meta.get("type") != null) {
            return " : \"" + meta.get("type") + "\"";
        }
        return "";
    }

    public static String formatDefinition(Map<String, Object> meta) {
        if (meta != null && meta.get("definition") != null) {
            String raw = String.valueOf(meta.get("definition"));
            String resolved = DefinitionResolver.resolve(raw);
            return " : \"" + resolved + "\"";
        }
        return "";
    }


    public static String formatMultiplicity(Map<String, Object> meta) {
        if (meta != null && meta.get("multiplicity") != null) {
            return " multiplicity [" + meta.get("multiplicity") + "]";
        }
        return "";
    }

    public static String formatDefaultValue(Map<String, Object> meta) {
        if (meta != null && meta.get("defaultValue") != null) {
            return " = " + meta.get("defaultValue");
        }
        return "";
    }

    public static String renderPartUsage(Element element, int indent) {
        String indentStr = DslRenderUtils.indent(indent);
        StringBuilder builder = new StringBuilder();
        Map<String, Object> meta = element.getMetadata();

        builder.append(indentStr)
                .append("PartUsage \"").append(element.getName()).append("\"")
                .append(formatDefinition(meta))
                .append(formatDirection(meta))
                .append(formatModifiers(element.getModifiers()))
                .append(" {\n");

        for (Element child : element.getChildren()) {
            builder.append(DslRendererRegistry.getRenderer(child.getType()).render(child, indent + 1));
        }
        builder.append(indentStr).append("}\n");
        return builder.toString();
    }


    public static String renderAttributeUsage(Element element, int indent) {
        String indentStr = DslRenderUtils.indent(indent);
        Map<String, Object> meta = element.getMetadata();

        return indentStr + "AttributeUsage \"" + element.getName() + "\""
                + formatType(meta)
                + formatDefaultValue(meta)
                + formatDirection(meta)
                + formatModifiers(element.getModifiers())
                + "\n";
    }



    public static String renderStructureDefinition(Element element, int indent) {
        String indentStr = DslRenderUtils.indent(indent);
        StringBuilder builder = new StringBuilder();

        builder.append(indentStr)
                .append("StructureDefinition \"").append(element.getName()).append("\" {\n");

        for (Element child : element.getChildren()) {
            builder.append(DslRendererRegistry.getRenderer(child.getType()).render(child, indent + 1));
        }

        builder.append(indentStr).append("}\n");
        return builder.toString();
    }

    public static String renderActionUsage(Element element, int indent) {
        String indentStr = DslRenderUtils.indent(indent);
        StringBuilder builder = new StringBuilder();
        Map<String, Object> meta = element.getMetadata();

        builder.append(indentStr)
                .append("ActionUsage \"").append(element.getName()).append("\"")
                .append(formatDefinition(meta))
                .append(" {\n").append(formatDirection(meta))
                .append(" ").append(formatModifiers(element.getModifiers()))
        ;

        for (Element child : element.getChildren()) {
            builder.append(DslRendererRegistry.getRenderer(child.getType()).render(child, indent + 1));
        }

        builder.append(indentStr).append("}\n");
        return builder.toString();
    }

    public static String renderValueDefinition(Element element, int indent) {
        String indentStr = DslRenderUtils.indent(indent);
        Map<String, Object> meta = element.getMetadata();

        return indentStr + "ValueDefinition \"" + element.getName() + "\""
                + (meta != null && meta.get("value") != null ? " = " + meta.get("value") : "")
                + "\n";
    }
    public static String formatDirection(Map<String, Object> meta) {
        if (meta != null && meta.get("direction") != null) {
            return " direction " + meta.get("direction");
        }
        return "";
    }

    public static String formatModifiers(Iterable<String> modifiers) {
        if (modifiers == null) return "";
        StringJoiner joiner = new StringJoiner(" ");
        for (String m : modifiers) {
            joiner.add(m);
        }
        String result = joiner.toString();
        return result.isEmpty() ? "" : " [" + result + "]";
    }



}

