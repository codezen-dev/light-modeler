package com.sysml.lightmodel.dsl.parser;

import java.util.*;
import java.util.regex.*;

public class DslTokenizer {

    private static final Pattern HEADER_PATTERN = Pattern.compile(
            "def\\s+(\\w+)\\s+(\\w+)\\s*\\{", Pattern.MULTILINE);

    public static List<DslRawEntry> tokenize(String dsl) {
        List<DslRawEntry> result = new ArrayList<>();

        int index = 0;
        while (index < dsl.length()) {
            Matcher matcher = HEADER_PATTERN.matcher(dsl);
            if (!matcher.find(index)) break;

            int start = matcher.start();
            String type = matcher.group(1);
            String name = matcher.group(2);
            int bodyStart = matcher.end() - 1; // position of the first {

            int braceCount = 0;
            int i = bodyStart;
            for (; i < dsl.length(); i++) {
                char c = dsl.charAt(i);
                if (c == '{') {
                    braceCount++;
                } else if (c == '}') {
                    braceCount--;
                    if (braceCount == 0) {
                        break;
                    }
                }
            }

            if (braceCount != 0) {
                throw new IllegalArgumentException("Unmatched braces in DSL input.");
            }

            int end = i + 1;
            String fullBlock = dsl.substring(start, end);
            String body = dsl.substring(bodyStart + 1, i).trim();

            DslRawEntry entry = new DslRawEntry();
            entry.type = type;
            entry.name = name;
            entry.header = fullBlock;
            entry.body = body;
            result.add(entry);

            index = end;
        }

        return result;
    }
}

