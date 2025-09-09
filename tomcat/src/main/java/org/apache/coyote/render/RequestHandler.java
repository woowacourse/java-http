package org.apache.coyote.render;

import java.util.Map;

public interface RequestHandler {
    String handle(String method, String path, Map<String, String> queryParams);
}
