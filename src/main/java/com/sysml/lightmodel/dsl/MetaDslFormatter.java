package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.Element;

import java.util.*;

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
            return " " + meta.get("multiplicity").toString();
        }
        return "";
    }




    public static String formatDefaultValue(Map<String, Object> meta) {
        if (meta != null && meta.get("defaultValue") != null) {
            return " = " + meta.get("defaultValue");
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

        // 合并修饰符（modifiers + visibility）
        Set<String> allMods = new LinkedHashSet<>();
        if (modifiers != null) allMods.addAll(modifiers);

        // ✅ 把 metadata 中的 modifiers 合并进来
        if (meta != null && meta.get("modifiers") instanceof List) {
            List<?> metaMods = (List<?>) meta.get("modifiers");
            for (Object mod : metaMods) {
                if (mod instanceof String) {
                    allMods.add((String) mod);
                }
            }
        }

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


}

