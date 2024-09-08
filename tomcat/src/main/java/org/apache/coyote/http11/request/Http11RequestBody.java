package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class Http11RequestBody {

    private final Map<String, String> bodyMap;

    public Http11RequestBody(Map<String, String> bodyMap) {
        this.bodyMap = bodyMap;
    }

    public static Http11RequestBody from(String bodyLine) {
        Map<String, String> map = new HashMap<>();
        if (bodyLine.isEmpty()) return new Http11RequestBody(map);

        for (String data : bodyLine.split("&")) {
            String[] keyValue = data.split("=");
            assert keyValue.length == 2;
            map.put(keyValue[0], keyValue[1]);
        }

        return new Http11RequestBody(map);
    }

    public String get(String key) {
        return bodyMap.get(key);
    }
}
