package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestHeader {

    private static final String REQUEST_HEADER_SEPARATOR = ": ";


    private final Map<String, String> httpRequestHeaders;

    private HttpRequestHeader(Map<String, String> httpRequestHeaders) {
        this.httpRequestHeaders = httpRequestHeaders;
    }

    public static HttpRequestHeader toHttpRequestHeader(List<String> requestHeaderLines) {
        Map<String, String> httpRequestHeader = new HashMap<>();
        for (String requestHeaderLine : requestHeaderLines) {
            String[] splitHeader = requestHeaderLine.split(REQUEST_HEADER_SEPARATOR);
            if (splitHeader.length != 2) {
                throw new IllegalArgumentException("잘못된 HTTP/1.1 헤더 양식입니다.");
            }
            httpRequestHeader.put(splitHeader[0], splitHeader[1]);
        }

        validateRequiredHeaderAttribute(httpRequestHeader);

        return new HttpRequestHeader(httpRequestHeader);
    }

    private static void validateRequiredHeaderAttribute(Map<String, String> httpRequestHeader) {
        if (!httpRequestHeader.containsKey("Host")) {
            throw new IllegalArgumentException("잘못된 HTTP/1.1 헤더 양식입니다.");
        }
    }

    public ContentType getContentType() {
        return ContentType.toContentType(httpRequestHeaders.get("Content-Type"));
    }

    public String getContentLength() {
        return httpRequestHeaders.get("Content-Length");
    }

    public Map<String, String> getMap() {
        return httpRequestHeaders;
    }

    public boolean containsKey(String key) {
        return httpRequestHeaders.containsKey(key);
    }
}
