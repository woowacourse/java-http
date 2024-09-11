package org.apache.coyote.adapter;

public class AdapterContext {
    private static Adapter adapter;

    public static void setAdapter(final Adapter adapter) {
        AdapterContext.adapter = adapter;
    }

    public static Adapter getAdapter() {
        return adapter;
    }
    private AdapterContext() {}
}
