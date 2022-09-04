package org.apache.coyote.http11.http11handler;

import java.util.Map;

public interface Http11Handler {

    default String http11Spec() {
        return "HTTP/1.1";
    }

    boolean isProperHandler(String uri);

    Map<String, String> extractElements(String uri);
}
