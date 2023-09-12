package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String ENTRY_DELIMITER = ";";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final String cookieHeader) {
        final Map<String, String> cookies = Pattern.compile(ENTRY_DELIMITER)
                .splitAsStream(cookieHeader.trim())
                .map(cookie -> cookie.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toUnmodifiableMap(cookie -> cookie[KEY_INDEX], cookie -> cookie[VALUE_INDEX]));
        return new HttpCookie(cookies);
    }

    public String getValue(final String key) {
        return cookies.get(key);
    }
}
