package org.apache.coyote.util;

import java.util.HashMap;
import java.util.Map;

public record RequestLine(
        String method,
        String path,
        Map<String, String> queryParameters,
        String httpVersion
) {

    public RequestLine(String method, String path, String httpVersion) {
        this(method, path, new HashMap<>(), httpVersion);
    }

}
