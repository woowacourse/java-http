package org.apache.coyote.http11.utils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.MimeType;

public class UriUtils {

    public static Map<String, String> getParameters(
            final String uri,
            final String queryString
    ) {
        Map<String, String> result = new HashMap<>(extractQueryString(uri));
        result.putAll(parseQueryString(queryString));
        return result;
    }

    public static Map<String, String> extractQueryString(final String uri) {
        if (!uri.contains("?")) {
            return Map.of();
        }
        final String[] split = uri.split("\\?");
        final String queryString = split.length > 1 ? split[1] : "";
        return parseQueryString(queryString);
    }

    public static Map<String, String> parseQueryString(final String queryString) {
        final Map<String, String> queryMap = new HashMap<>();
        if (queryString == null || queryString.isBlank()) {
            return queryMap;
        }

        final String[] pairs = queryString.split("&");

        for (final String pair : pairs) {
            final String[] keyValue = pair.split("=", 2);
            final String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8) : "";
            queryMap.put(key, value);
        }
        return queryMap;
    }

    public static String extractExtension(final String resourceName) {
        int dotIndex = resourceName.lastIndexOf(".");
        if (dotIndex == -1) {
            return "";
        }
        return resourceName.substring(dotIndex + 1);
    }

    public static MimeType getMimeType(final String resourceName) {
        return MimeType.getOrDefault(extractExtension(resourceName));
    }
}
