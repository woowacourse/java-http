package org.apache.coyote.http11.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestBody {

    private final String content;

    public HttpRequestBody(String content) {
        this.content = content;
    }

    public Map<String, String> parseFormData() {
        Map<String, String> parameters = new HashMap<>();

        for (String parameter : content.split("&")) {
            String[] parts = parameter.split("=", 2);

            if (parts.length != 2) {
                continue;
            }

            String name = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);

            parameters.put(name, value);
        }

        return Map.copyOf(parameters);
    }

    public String getContent() {
        return content;
    }
}
