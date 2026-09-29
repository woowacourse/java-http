package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private final String content;

    public RequestBody(String content) {
        this.content = content;
    }

    public Map<String, String> parseParams() {
        Map<String, String> params = new HashMap<>();
        for (String param : content.split("&")) {
            String[] keyValue = param.split("=", 2);
            if (keyValue.length != 2 || keyValue[1].isBlank()) {
                continue;
            }
            params.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
        }
        return params;
    }

    public String getContent() {
        return content;
    }
}
