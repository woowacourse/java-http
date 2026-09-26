package org.apache.coyote.http11.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

final class KeyValueParser {

    private static final String KEY_VALUE_DELIMITER = "=";

    private KeyValueParser() {
    }

    static Map<String, String> parse(String value, String delimiter) {
        Map<String, String> parsed = new HashMap<>();
        for (String pair : value.split(delimiter)) {
            String[] keyAndValue = pair.split(KEY_VALUE_DELIMITER, 2);
            if (keyAndValue.length != 2) {
                continue;
            }

            parsed.put(
                URLDecoder.decode(keyAndValue[0].strip(), StandardCharsets.UTF_8),
                URLDecoder.decode(keyAndValue[1].strip(), StandardCharsets.UTF_8));
        }
        return parsed;
    }
}
