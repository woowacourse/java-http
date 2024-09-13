package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.HttpHeader.CONTENT_TYPE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.ContentType;

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
            validateHeaderForm(splitHeader);
            httpRequestHeader.put(splitHeader[0], splitHeader[1]);
        }

        validateRequiredHeaderAttribute(httpRequestHeader);
        return new HttpRequestHeader(httpRequestHeader);
    }

    private static void validateHeaderForm(String[] splitHeader) {
        if (splitHeader.length != 2) {
            throw new IllegalArgumentException("각 헤더는 key:value쌍으로 이루어져야 합니다.");
        }
    }

    private static void validateRequiredHeaderAttribute(Map<String, String> httpRequestHeader) {
        if (!httpRequestHeader.containsKey("Host")) {
            throw new IllegalArgumentException("Host 헤더는 필수입니다.");
        }
    }

    public ContentType getContentType() {
        return ContentType.toContentType(httpRequestHeaders.get(CONTENT_TYPE.getHttpForm()));
    }

    public String getContentLength() {
        return httpRequestHeaders.get(CONTENT_LENGTH.getHttpForm());
    }

    public String getValue(String name) {
        return httpRequestHeaders.get(name);
    }

    public boolean isContainKey(String key) {
        return httpRequestHeaders.containsKey(key);
    }
}
