package org.apache.coyote.http11.request;


import java.util.HashMap;
import java.util.Map;

public record RequestBody(
        Map<String, String> bodyMap
) {

    public static RequestBody parse(String body) {
        Map<String, String> bodyMap = new HashMap<>();
        if (!body.isBlank()) {
            String[] split = body.split("&");
            for (String s : split) {
                String[] keyValue = s.split("=");
                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = keyValue[1];
                    bodyMap.put(key, value);
                }
            }
        }
        return new RequestBody(bodyMap);
    }

    public String getValueByKey(String key) {
        return bodyMap.getOrDefault(key, "");
    }
}
