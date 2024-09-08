package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Http11ResponseHeaders {

    private final Map<String, String> headerMap;

    private Http11ResponseHeaders(Map<String, String> headers) {
        this.headerMap = new LinkedHashMap<>();
        headerMap.putAll(headers);
    }

    public static Http11ResponseHeaders instance() {
        return new Http11ResponseHeaders(new HashMap<>());
    }

    public static Http11ResponseHeadersBuilder builder() {
        return new Http11ResponseHeadersBuilder();
    }

    public void addHeader(String key, String value) {
        headerMap.put(key, value);
    }

    public String asString() {
        final StringBuilder sb = new StringBuilder();
        headerMap.forEach((key, value) -> sb.append(key).append(": ").append(value).append(" \r\n"));
        return sb.toString();
    }

    public static class Http11ResponseHeadersBuilder {

        private final Map<String, String> headers;

        private Http11ResponseHeadersBuilder() {
            this.headers = new LinkedHashMap<>();
        }

        public Http11ResponseHeadersBuilder addHeader(String header, String value) {
            headers.put(header, value);
            return this;
        }

        public Http11ResponseHeaders build() {
            return new Http11ResponseHeaders(headers);
        }
    }
}
