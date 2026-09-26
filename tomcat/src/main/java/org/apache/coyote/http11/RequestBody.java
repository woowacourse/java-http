package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public record RequestBody(String content) {

    public Map<String, String> parseFormData() {
        Map<String, String> formData = new HashMap<>();

        if (content == null || content.isBlank()) {
            return formData;
        }

        String[] parameters = content.split("&");

        for (String parameter : parameters) {
            String[] values = parameter.split("=", 2);

            if (values.length != 2) {
                continue;
            }

            String key = URLDecoder.decode(values[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(values[1], StandardCharsets.UTF_8);

            formData.put(key, value);
        }

        return formData;
    }
}
