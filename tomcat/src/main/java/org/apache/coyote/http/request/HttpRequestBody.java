package org.apache.coyote.http.request;

import static common.HttpConstants.KEY_VALUE_SEPARATOR;
import static common.HttpConstants.PARAM_SEPARATOR;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import common.ContentType;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpRequestBody {

    private final Map<String, String> params;

    public static HttpRequestBody from(final String rawBody, final ContentType contentType) {
        final Map<String, String> values = parseBody(rawBody, contentType);
        return new HttpRequestBody(values);
    }

    public static HttpRequestBody empty() {
        return new HttpRequestBody(new HashMap<>());
    }

    private static Map<String, String> parseBody(final String rawBody, final ContentType contentType) {
        final Map<String, String> map = new HashMap<>();

        if (rawBody == null || rawBody.trim().isEmpty()) {
            return map;
        }

        if (contentType == ContentType.APPLICATION_X_WWW_FORM_URLENCODED) {
            parseFormData(rawBody, map);
        }

        return map;
    }

    private static void parseFormData(final String rawBody, final Map<String, String> map) {
        final String[] pairs = rawBody.split(PARAM_SEPARATOR);
        for (final String pair : pairs) {
            final int equalIndex = pair.indexOf(KEY_VALUE_SEPARATOR);
            if (equalIndex <= 0) {
                continue;
            }
            final String key = URLDecoder.decode(pair.substring(0, equalIndex), StandardCharsets.UTF_8);
            final String value = URLDecoder.decode(pair.substring(equalIndex + 1), StandardCharsets.UTF_8);
            map.put(key, value);
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
        params.forEach((key, value) -> {
            sb.append(key).append(KEY_VALUE_SEPARATOR).append(value).append(PARAM_SEPARATOR);
        });
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
