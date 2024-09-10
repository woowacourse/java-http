package org.apache.coyote.http11.httpresponse;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeaders {

    private final Map<String, String> headers;

    public ResponseHeaders(String contentType, String responseBody) {
        Map<String, String> headers = new LinkedHashMap<>();

        headers.put("Content-Type", contentType + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(responseBody.getBytes().length));

        this.headers = headers;
    }

    public String getMessage() {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
