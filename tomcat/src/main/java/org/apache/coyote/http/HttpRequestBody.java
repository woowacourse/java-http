package org.apache.coyote.http;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static org.apache.coyote.http.HttpConstants.*;

@Getter
@RequiredArgsConstructor
public class HttpRequestBody {

    private final Map<String, String> params;

    public static HttpRequestBody from(final String rawBody, final ContentType contentType) {
        final Map<String, String> values = parseBody(rawBody, contentType);
        return new HttpRequestBody(values);
    }

    private static Map<String, String> parseBody(final String rawBody, final ContentType contentType) {
        final Map<String, String> map = new HashMap<>();

        if (rawBody == null || rawBody.isEmpty()) {
            return map;
        }

        if (contentType == ContentType.FORM_URLENCODED) {
            parseQueryString(rawBody, map);
        }

        return map;
    }

    private static void parseQueryString(final String queryString, final Map<String, String> params) {
        if (queryString == null || queryString.isEmpty()) {
            return;
        }

        final String[] pairs = queryString.split(PARAM_SEPARATOR);
        for (final String pair : pairs) {
            parseKeyValuePair(pair, params);
        }
    }

    private static void parseKeyValuePair(final String pair, final Map<String, String> params) {
        final int equalIndex = pair.indexOf(KEY_VALUE_SEPARATOR);
        if (equalIndex != -1) {
            final String key = URLDecoder.decode(pair.substring(0, equalIndex), StandardCharsets.UTF_8);
            final String value = URLDecoder.decode(pair.substring(equalIndex + 1), StandardCharsets.UTF_8);
            params.put(key, value);
        }
    }

    public String getValue(final String name) {
        return params.getOrDefault(name, "");
    }

    @Override
    public String toString() {
        if (params.isEmpty()) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();

        params.forEach((key, value) ->
                sb.append(key).append(KEY_VALUE_SEPARATOR).append(value).append(PARAM_SEPARATOR));

        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }
}
