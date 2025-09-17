package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class HttpRequestParser {

    public static Map<String, String> parseParameters(final String parameterString) {
        final Map<String, String> params = new HashMap<>();

        if (parameterString == null || parameterString.isEmpty()) {
            return params;
        }

        for (String pair : parameterString.split("&")) {
            final int index = pair.indexOf('=');
            if (index == -1) {
                continue;
            }

            final String key = URLDecoder.decode(pair.substring(0, index), StandardCharsets.UTF_8);
            final String value = URLDecoder.decode(pair.substring(index + 1), StandardCharsets.UTF_8);
            params.put(key, value);
        }

        return params;
    }

    private HttpRequestParser() {
    }
}
