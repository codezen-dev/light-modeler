package com.sysml.lightmodel.semantic;

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
}
