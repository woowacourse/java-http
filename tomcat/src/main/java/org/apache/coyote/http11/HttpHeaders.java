package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpHeaders {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = new LinkedHashMap<>(headers);
    }

    public String buildHttpHeadersResponse() {
        return String.join("\r\n",
                headers.entrySet().stream()
                        .map(entry -> String.format("%s: %s ", entry.getKey(), entry.getValue()))
                        .toArray(String[]::new)
        );
    }
}
