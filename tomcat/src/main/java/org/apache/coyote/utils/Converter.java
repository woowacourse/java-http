package org.apache.coyote.utils;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Converter {

    private Converter() {
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


    public static URL convertPathToUrl(String path) {
        if (!path.contains(".")) {
            path += ".html";
        }
        return Converter.class.getClassLoader().getResource("static" + path);
    }
}
