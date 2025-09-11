package org.apache.coyote.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class UrlEncodedFormParser {

    public static Map<String, String> parse(final String urlEncodedData) {
        if (urlEncodedData == null || urlEncodedData.trim().isEmpty()) {
            return Map.of();
        }

        return Arrays.stream(urlEncodedData.split("&"))
                .filter(pair -> !pair.trim().isEmpty())
                .map(pair -> pair.split("=", 2))
                .filter(parts -> parts.length == 2)
                .collect(
                        Collectors.toMap(
                                parts -> decode(parts[0]),
                                parts -> decode(parts[1])
                        )
                );
    }

    private static String decode(final String encoded) {
        try {
            return URLDecoder.decode(encoded, StandardCharsets.UTF_8);
        } catch (final Exception e) {
            return encoded;
        }
    }
}
