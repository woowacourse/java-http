package org.apache.catalina.cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import org.apache.commons.lang3.StringUtils;

public class CookieUtils {
    private static final String VALUE_DELIMITER = "; ";
    private static final String COMPONENT_DELIMITER = "=";

    private CookieUtils() {
    }

    public static Cookie createCookie(String text) {
        if (StringUtils.isBlank(text)) {
            return new Cookie();
        }
        Map<String, String> values = parseValues(text);
        return new Cookie(values);
    }

    private static Map<String, String> parseValues(String text) {
        Map<String, String> values = new HashMap<>();
        for (String pair : text.split(VALUE_DELIMITER)) {
            String[] cookieComponents = pair.split(COMPONENT_DELIMITER);
            String key = cookieComponents[0];
            String value = cookieComponents[1];
            values.put(key, value);
        }
        return values;
    }

    public static String toValues(Cookie cookie) {
        Map<String, String> values = cookie.getValues();
        StringJoiner valueJoiner = new StringJoiner(VALUE_DELIMITER);
        for (var entry : values.entrySet()) {
            String value = entry.getKey() + COMPONENT_DELIMITER + entry.getValue();
            valueJoiner.add(value);
        }
        return valueJoiner.toString();
    }
}
