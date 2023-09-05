package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestCookies {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String COOKIE_DELIMITER = "; ";

    private final Map<String, String> cookies;

    private HttpRequestCookies(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpRequestCookies empty() {
        return new HttpRequestCookies(new HashMap<>());
    }

    public static HttpRequestCookies of(final String cookies) {
        Map<String, String> httpCookies = new HashMap<>();
        String[] pairs = cookies.split(COOKIE_DELIMITER);
        for (String pair : pairs) {
            String[] cookie = pair.split(KEY_VALUE_DELIMITER);
            httpCookies.put(cookie[KEY_INDEX].strip(), cookie[VALUE_INDEX]);
        }
        return new HttpRequestCookies(httpCookies);
    }

    public String get(final String key) {
        return cookies.get(key);
    }
}
