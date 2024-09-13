package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.Map;

public record HttpRequestUri(String path, Map<String, String> queryParams) {

    public HttpRequestUri(String path) {
        this(path, Collections.emptyMap());
    }
}
