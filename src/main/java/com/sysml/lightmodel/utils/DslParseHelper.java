package com.sysml.lightmodel.utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DslParseHelper {

    private static final Pattern MULTIPLICITY_PATTERN = Pattern.compile("\\[([^\\]]+)]");
    private static final Pattern METADATA_PATTERN = Pattern.compile("\\{([^}]*)}");
    private static final Pattern DOC_PATTERN = Pattern.compile("doc\\s+\"([^\"]*)\"");
    private static final Pattern EXPR_PATTERN = Pattern.compile("expr\\s+\"([^\"]*)\"");

    public static String parseMultiplicity(String line) {
        Matcher matcher = MULTIPLICITY_PATTERN.matcher(line);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    public static Map<String, Object> parseMetadata(String line) {
        Matcher matcher = METADATA_PATTERN.matcher(line);
        Map<String, Object> metadata = new LinkedHashMap<>();
        if (matcher.find()) {
            String[] entries = matcher.group(1).split(",");
            for (String entry : entries) {
                String[] kv = entry.trim().split("=", 2);
                if (kv.length == 2) {
                    String key = kv[0].trim();
                    String value = kv[1].trim().replaceAll("^\"|\"$", "");
                    metadata.put(key, value);
                }
            }
        }
        return metadata;
    }

    public static String parseDocumentation(String line) {
        Matcher matcher = DOC_PATTERN.matcher(line);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    public static String parseExpression(String line) {
        Matcher matcher = EXPR_PATTERN.matcher(line);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    public static Map<String, Object> enrichMetadata(String line) {
        Map<String, Object> metadata = DslParseHelper.parseMetadata(line);
        String multiplicity = DslParseHelper.parseMultiplicity(line);
        if (multiplicity != null) {
            metadata.put("multiplicity", multiplicity);
        }
        return metadata;
    }

}
