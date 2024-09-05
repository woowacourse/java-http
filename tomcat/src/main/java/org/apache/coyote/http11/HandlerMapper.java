package org.apache.coyote.http11;

import java.util.List;

class HandlerMapper {

    private static final List<String> nonStaticPaths = List.of("/login");

    public static boolean isNonStaticRequest(String path) {
        return nonStaticPaths.contains(path);
    }
}
