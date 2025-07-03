package com.sysml.lightmodel.semantic;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionVariableExtractor {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\b([a-zA-Z_][a-zA-Z0-9_]*)\\b");

    public static Set<String> extractVariables(String expression) {
        Set<String> variables = new HashSet<>();
        if (expression == null || expression.isEmpty()) return variables;

        Matcher matcher = VARIABLE_PATTERN.matcher(expression);
        while (matcher.find()) {
            String var = matcher.group(1);
            // 排除关键字或操作符（可扩展）
            if (!isKeyword(var)) {
                variables.add(var);
            }
        }
        return variables;
    }

    private static boolean isKeyword(String word) {
        return Set.of("true", "false", "null", "if", "else", "and", "or").contains(word);
    }
}

