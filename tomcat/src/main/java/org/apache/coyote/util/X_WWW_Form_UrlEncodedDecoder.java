package org.apache.coyote.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class X_WWW_Form_UrlEncodedDecoder {

    private static final String DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private X_WWW_Form_UrlEncodedDecoder() {
    }

    public static Map<String, String> parse(final String input) {
        final Map<String, String> result = new HashMap<>();

        final String[] properties = input.split(DELIMITER);
        for (final String property : properties) {
            final String[] keyAndValue = property.split(KEY_VALUE_DELIMITER);

            if (keyAndValue.length > 1) {
                final String key = URLDecoder.decode(keyAndValue[0], StandardCharsets.UTF_8);
                final String value = URLDecoder.decode(keyAndValue[1], StandardCharsets.UTF_8);
                result.put(key, value);
            }
        }

        return result;
    }
}
