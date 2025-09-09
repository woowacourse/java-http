package org.apache.coyote.handler;

import java.util.Map;

public interface RequestHandler {
    String handle(String method, String path, Map<String, String> queryParams);
}
