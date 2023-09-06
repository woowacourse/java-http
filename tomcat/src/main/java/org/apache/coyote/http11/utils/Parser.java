package org.apache.coyote.http11.utils;

import java.util.HashMap;
import java.util.Map;

public class Parser {

    private Parser() {
    }

    public static Map<String, String> parseFormData(final String formData) {
        final Map<String, String> fields = new HashMap<>();
        if (!formData.contains("&")) {
            return fields;
        }

        for (final String field : formData.split("&")) {
            final String[] param = field.split("=", 2);
            final String name = param[0];
            final String value = param[1];
            fields.put(name, value);
        }
        return fields;
    }
}
