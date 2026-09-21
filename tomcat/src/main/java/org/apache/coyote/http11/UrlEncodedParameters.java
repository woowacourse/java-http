package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

final class UrlEncodedParameters {

    private UrlEncodedParameters() {
    }

    static Map<String, String> parse(String value) {
        if (value == null || value.isEmpty()) {
            return Map.of();
        }

        Map<String, String> parameters = new HashMap<>();
        for (String parameter : value.split("&")) {
            String[] pair = parameter.split("=", 2);
            if (pair.length != 2) {
                continue;
            }

            parameters.put(decode(pair[0]), decode(pair[1]));
        }
        return Map.copyOf(parameters);
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new HttpRequestParseException("잘못된 URL 인코딩입니다.", e);
        }
    }
}
