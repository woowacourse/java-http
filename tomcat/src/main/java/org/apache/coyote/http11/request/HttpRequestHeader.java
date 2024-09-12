package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestHeader {
    private final Map<String, String> headers;

    public HttpRequestHeader(List<String> lines) {
        headers = new HashMap<>();
        for( String line : lines) {
            int index = line.indexOf(":");
            headers.put(line.substring(0, index).trim(), line.substring(index + 1).trim());
        }
    }

    public String getContentLength() {
        return headers.get("Content-Length");
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
