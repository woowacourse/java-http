package org.apache.catalina.cookie;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class CookieCreator {

    private CookieCreator() {
    }

    public static Cookie create(String text) {
        if (StringUtils.isBlank(text)) {
            return new Cookie();
        }
        Map<String, String> values = parseValues(text);
        return new Cookie(values);
    }

    private static Map<String, String> parseValues(String text) {
        Map<String, String> values = new HashMap<>();
        for (String pair : text.split("; ")) {
            String[] cookieComponents = pair.split("=");
            String key = cookieComponents[0];
            String value = cookieComponents[1];
            values.put(key, value);
        }
        return values;
    }
}
