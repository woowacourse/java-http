package org.apache.coyote.http.request;

import static common.HttpConstants.COOKIE_HEADER_NAME;
import static common.HttpConstants.HEADER_VALUE_SEPARATOR;
import static common.HttpConstants.KEY_VALUE_SEPARATOR;
import static common.HttpConstants.VALUE_SEPARATOR;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpCookie {

    private final Map<String, String> values;

    public static HttpCookie from(final String rawCookie) {
        final Map<String, String> cookies = new HashMap<>();

        if (rawCookie == null || rawCookie.isEmpty()) {
            return new HttpCookie(cookies);
        }

        final String[] pairs = rawCookie.split(VALUE_SEPARATOR);
        for (final String pair : pairs) {
            final Entry<String, String> cookie = parseCookieLine(pair);
            cookies.put(cookie.getKey(), cookie.getValue());
        }
        return new HttpCookie(cookies);
    }

    private static Entry<String, String> parseCookieLine(final String pair) {
        final int equalIndex = pair.indexOf(KEY_VALUE_SEPARATOR);
        if (equalIndex != -1) {
            final String key = pair.substring(0, equalIndex).trim();
            final String value = pair.substring(equalIndex + 1).trim();
            return new SimpleEntry<>(key, value);
        }
        throw new IllegalArgumentException("쿠키 형식이 올바르지 않습니다: " + pair);
    }

    public String get(final String name) {
        return values.getOrDefault(name, "");
    }

    @Override
    public String toString() {
        if (values.isEmpty()) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();
        sb.append(COOKIE_HEADER_NAME).append(HEADER_VALUE_SEPARATOR).append(" ");

        values.forEach((key, value) ->
                sb.append(key).append(KEY_VALUE_SEPARATOR).append(value).append(VALUE_SEPARATOR).append(" "));

        sb.deleteCharAt(sb.length() - 2); // last (; + space)

        return sb.toString();
    }
}
