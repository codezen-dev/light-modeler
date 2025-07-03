package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.DefinitionResolver;
import com.sysml.lightmodel.semantic.DslRenderUtils;
import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.semantic.ExpressionVariableExtractor;

import java.util.*;
import java.util.stream.Collectors;

public class MetaDslFormatter {

    public static String getString(Element element, int indent, String indentStr, StringBuilder builder, Map<String, Object> meta) {
        if (meta != null && meta.containsKey("expression")) {
            builder.append(" where ").append(meta.get("expression"));
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
            String type = meta.get("type").toString();
            boolean unresolved = Boolean.TRUE.equals(meta.get("typeUnresolved"));
            return " : \"" + type + (unresolved ? " /* unresolved */" : "") + "\"";
        }
        return "";
    }


    public static String formatDefinition(Map<String, Object> meta) {
        if (meta != null && meta.get("definition") != null) {
            String def = meta.get("definition").toString();
            boolean unresolved = Boolean.TRUE.equals(meta.get("definitionUnresolved"));
            return " : \"" + def + (unresolved ? " /* unresolved */" : " (StructureDefinition)") + "\"";
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

    public static String formatDirectionAndModifiers(Map<String, Object> meta, List<String> modifiers) {
        StringBuilder sb = new StringBuilder();

        // direction
        if (meta != null && meta.get("direction") != null) {
            sb.append(" direction ").append(meta.get("direction"));
        }

        // 收集修饰符（modifiers + visibility）
        Set<String> allMods = new LinkedHashSet<>();
        if (modifiers != null) allMods.addAll(modifiers);

        if (meta != null && meta.get("visibility") != null) {
            allMods.add(meta.get("visibility").toString());
        }

        if (!allMods.isEmpty()) {
            sb.append(" [");
            sb.append(String.join(" ", allMods));
            sb.append("]");
        }

        return sb.toString();
    }

    public static String renderConstraintUsage(Element element, int indent) {
        String indentStr = DslRenderUtils.indent(indent);
        StringBuilder builder = new StringBuilder();

        builder.append(indentStr)
                .append("ConstraintUsage \"").append(element.getName()).append("\"");

        Map<String, Object> metadata = element.getMetadata();
        String expression = metadata != null ? (String) metadata.get("expression") : null;

        if (expression != null) {
            builder.append(" where ").append(expression);

            // ✅ 提取变量并尝试解析引用
            Set<String> vars = ExpressionVariableExtractor.extractVariables(expression);
            if (!vars.isEmpty()) {
                builder.append("  // refs: ");
                for (String var : vars) {
                    Element match = findElementByName(element.getChildren(), var);
                    if (match != null) {
                        builder.append(var).append("->").append(match.getType()).append(", ");
                    } else {
                        builder.append(var).append("->? , ");
                    }
                }
            }
        }

        builder.append(" {\n");
        for (Element child : element.getChildren()) {
            builder.append(DslRendererRegistry.getRenderer(child.getType()).render(child, indent + 1));
        }
        builder.append(indentStr).append("}\n");

        return builder.toString();
    }

    private static Element findElementByName(List<Element> children, String name) {
        if (children == null) return null;
        for (Element child : children) {
            if (name.equals(child.getName())) {
                return child;
            }
        }
        return null;
    }



}

