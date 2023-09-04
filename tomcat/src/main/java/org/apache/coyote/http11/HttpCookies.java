package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookies {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private final Map<String, String> httpCookies;
    public static HttpCookies empty() {
        return new HttpCookies(new HashMap<>());
    }


    private HttpCookies(final Map<String, String> httpCookies) {
        this.httpCookies = httpCookies;
    }

    public static HttpCookies of(final String cookies) {
        Map<String, String> httpCookies = new HashMap<>();
        String[] partCookies = cookies.split(";");
        for (String cookie : partCookies) {
            String[] split = cookie.split("=");
            httpCookies.put(split[KEY_INDEX].strip(), split[VALUE_INDEX].strip());
        }
        return new HttpCookies(httpCookies);
    }

    public String get(String key) {
        return httpCookies.get(key);
    }
}
