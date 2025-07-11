package com.sysml.lightmodel.semantic;

import com.sysml.lightmodel.dsl.RendererContext;

import java.util.Map;

public class DslRenderUtils {
    public static String indent(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }
    public static void appendDocumentation(StringBuilder builder, Element element, String indentStr) {
        if (element.getDocumentation() != null) {
            builder.append(indentStr)
                    .append("// ").append(element.getDocumentation())
                    .append("\n");
        }
    }

    public static String resolveDefinition(Map<String, Object> meta) {
        if (meta == null) return "";

        Object defId = meta.get("definitionId");

        if (defId == null) return "";

        Element def = RendererContext.getResolver().resolveById(defId);
        if (def != null) {
            return " : \"" + def.getName() + " (" + def.getType() + ")\"";
        } else {
            return " : \"" + defId + " /* unresolved */\"";
        }
    }

    public static String resolveType(Map<String, Object> meta) {
        if (meta == null) return "";

        Object typeId = meta.get("typeId");
        boolean unresolved = Boolean.TRUE.equals(meta.get("typeUnresolved"));
        if (typeId != null) {
            Element type = RendererContext.getResolver().resolveById(typeId);
            if (type != null) {
                return " : \"" + type.getName() + " (" + type.getType() + ")\"";
            } else {
                return " : \"" + typeId + " /* unresolved */\"";
            }
        }

        // ✅ 新增：支持直接字符串类型
        if (meta.get("type") instanceof String strType) {
            return " : \"" + strType + "\"";
        }

        return "";
    }


}
