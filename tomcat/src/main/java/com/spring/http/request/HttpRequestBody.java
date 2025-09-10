package com.spring.http.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public record HttpRequestBody(String content) {

    public Map<String, String> parseFormData() {
        Map<String, String> result = new HashMap<>();
        if (content == null || content.isEmpty()) {
            return result;
        }

        processDecodeResult(result);
        return result;
    }

    private void processDecodeResult(Map<String, String> result) {
        for (String pair : content.split("&")) {
            String[] kv = pair.split("=", 2);
            String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            if (kv.length == 2) {
                result.put(key, URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
                continue;
            }
            if (kv.length == 1) {
                result.put(key, "");
            }
        }
    }
}
