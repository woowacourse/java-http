package org.apache.coyote.http11;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class HttpHeaders {

    private final Map<String, String> values;

    public HttpHeaders(Map<String, String> values) {
        Map<String, String> caseInsensitiveValues = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        caseInsensitiveValues.putAll(values);
        this.values = Collections.unmodifiableMap(caseInsensitiveValues);
    }

    public String get(String name) {
        return values.get(name);
    }

    public int getContentLength() {
        String contentLength = get("Content-Length");
        if (contentLength == null || contentLength.isBlank()) {
            return 0;
        }
        return Integer.parseInt(contentLength);
    }

    public Map<String, String> values() {
        return values;
    }
}
