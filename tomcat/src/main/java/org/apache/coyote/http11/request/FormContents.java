package org.apache.coyote.http11.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FormContents {

    private final Map<String, String> values;

    private FormContents(Map<String, String> values) {
        this.values = values;
    }

    public static FormContents from(String body) {
        Map<String, String> values = new HashMap<>();

        if (body == null || body.isEmpty()) {
            return new FormContents(values);
        }

        for (String parameter : body.split("&")) {
            String[] keyValue = parameter.split("=", 2);
            if (keyValue.length == 2) {
                values.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
            }
        }

        return new FormContents(values);
    }

    public Optional<String> find(String name) {
        return Optional.ofNullable(values.get(name));
    }
}
