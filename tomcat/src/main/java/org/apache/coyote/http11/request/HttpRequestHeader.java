package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.response.HttpResponseHeaderNames.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.HttpResponseHeaderNames.COOKIE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestHeader {

    private static final String HEADER_DELIMITER = ":";

    private final Map<String, String> headers;

    public HttpRequestHeader(List<String> lines) {
        headers = new HashMap<>();
        for (String line : lines) {
            int index = line.indexOf(HEADER_DELIMITER);
            headers.put(line.substring(0, index).trim(), line.substring(index + 1).trim());
        }
    }

    public String getContentLength() {
        return headers.get(CONTENT_LENGTH.getHeaderName());
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getCookies() {
        if (headers.containsKey(COOKIE.getHeaderName())) {
            return null;
        }
        return headers.get(COOKIE.getHeaderName());
    }
}
