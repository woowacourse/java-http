package org.apache.coyote.http11.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestBody {

    private String value;

    public RequestBody(String body) {
        this.value = body;
    }

    public Map<String, String> getBodyParams() {
        return parseFormData(value);
    }

    private Map<String, String> parseFormData(String body) {
        Map<String, String> result = new LinkedHashMap<>();
        if (body == null || body.isEmpty()) {
            return result;
        }

        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);

            String value = keyValue.length > 1
                    ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)
                    : "";
            result.put(key, value);
        }

        return result;
    }

    public String getValue() {
        return value;
    }
}
