package org.apache.coyote.handler;

import java.util.List;

public class HandlerMapping {

    private static final List<String> nonStaticPaths = List.of("/login");

    public static boolean isNonStaticRequest(String path) {
        return nonStaticPaths.contains(path);
    }
}
