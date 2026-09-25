package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public record HttpRequestBody(
        String requestBody
) {
    private static final HttpRequestBody EMPTY = new HttpRequestBody("");

    public static HttpRequestBody empty() {
        return EMPTY;
    }

    public static HttpRequestBody from(BufferedReader br, Integer contentLength) throws IOException {
        String requestBody;
        char[] buffer = new char[contentLength];
        int offset = 0;
        while (offset < contentLength) {
            int result = br.read(buffer, offset, contentLength - offset);
            if (result == -1) {
                break;
            }
            offset += result;
        }
        requestBody = new String(buffer);

        return new HttpRequestBody(requestBody);
    }

    public Map<String, String> formData() {
        Map<String, String> params = new HashMap<>();
        if (requestBody.isBlank()) {
            return params;
        }
        for (String pair : requestBody.split("&")) {
            String key = pair.split("=", 2)[0];
            String value = pair.split("=", 2)[1];
            params.put(key, value);
        }
        return params;
    }
}
