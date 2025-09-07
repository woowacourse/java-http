package org.apache.coyote.render;

public interface HttpRequestHandler {
    String handle(String method, String path);
}
