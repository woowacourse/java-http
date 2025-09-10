package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestBodyUtils {

    public static Map<String, String> parseFormUrlEncoded(final String formRequestBody) {
        if (formRequestBody == null || formRequestBody.isBlank()) {
            return Collections.emptyMap();
        }
        final Map<String, String> parameters = new HashMap<>();

        for (final String pair : formRequestBody.split("&")) {
            final String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                final String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                final String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                parameters.put(key, value);
            }
        }
        return parameters;
    }
}
