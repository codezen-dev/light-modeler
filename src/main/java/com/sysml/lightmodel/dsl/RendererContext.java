package com.sysml.lightmodel.dsl;

public class RendererContext {
    private static final ThreadLocal<DefinitionResolver> resolverHolder = new ThreadLocal<>();

    public static void setResolver(DefinitionResolver resolver) {
        resolverHolder.set(resolver);
    }

    public static DefinitionResolver getResolver() {
        return resolverHolder.get();
    }

    public static void clear() {
        resolverHolder.remove();
    }
}
