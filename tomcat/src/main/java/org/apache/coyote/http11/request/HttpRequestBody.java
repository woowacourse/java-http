package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public record HttpRequestBody(Map<String, String> body) {

    public HttpRequestBody(String payload, String contentType) {
        this(parseBody(payload, contentType));
    }

    private static Map<String, String> parseBody(String payload, String contentType) {
        Map<String, String> body = new HashMap<>();
        if ("application/x-www-form-urlencoded".equals(contentType)) {
            String[] payloads = payload.split("&");
            Arrays.stream(payloads)
                    .map(param -> param.split("="))
                    .filter(parts -> parts.length == 2)
                    .forEach(parts -> body.put(parts[0], parts[1]));
        }

        return body;
    }

    public String getAttribute(String key) {
        return body.get(key);
    }
}
