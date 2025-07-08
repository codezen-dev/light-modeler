package com.sysml.lightmodel.semantic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将 definitionName → resolvedDefinition 映射补全
 */
public class DefinitionBinder {

    public static void bindAll(List<Element> allElements) {
        Map<String, Definition> nameMap = new HashMap<>();

        // 构建 name → definition 的映射
        for (Element e : allElements) {
            if (e instanceof Definition def && def.getName() != null) {
                nameMap.put(def.getName(), def);
            }
        }

        // 扫描 Usage，进行绑定
        for (Element e : allElements) {
            if (e instanceof Definition def && def.getOwnedUsages() != null) {
                for (Usage usage : def.getOwnedUsages()) {
                    String defName = usage.getDefinitionName();
                    if (defName != null && nameMap.containsKey(defName)) {
                        usage.setResolvedDefinition(nameMap.get(defName));
                    }
                }
            }
        }
    }
}

