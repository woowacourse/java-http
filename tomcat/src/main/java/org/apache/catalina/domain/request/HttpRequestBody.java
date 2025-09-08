package org.apache.catalina.domain.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public record HttpRequestBody(String content) {

    public Map<String, String> parseFormData() {
        final String decode = URLDecoder.decode(content, StandardCharsets.UTF_8);
        Map<String, String> result = new java.util.HashMap<>();
        for (String pair : decode.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                result.put(kv[0], kv[1]);
                continue;
            }
            if (kv.length == 1) {
                result.put(kv[0], "");
            }
        }
        return result;
    }
}
