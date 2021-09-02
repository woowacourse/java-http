package nextstep.jwp.web.http.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.exception.NoMatchingElement;

public class HttpRequestCookie {

    private static final int KEY = 0;
    private static final int VALUE = 1;

    private Map<String, String> cookies = new HashMap<>();

    public HttpRequestCookie() {
    }

    public HttpRequestCookie(String rawCookies) {
        cookies = parseCookie(rawCookies);
    }

    private Map<String, String> parseCookie(String rawCookies) {
        Map<String, String> cookies = new HashMap<>();

        String[] values = rawCookies.split(";");
        for (String value : values) {
            String[] keyAndValue = value.split("=");
            cookies.put(keyAndValue[KEY], keyAndValue[VALUE]);
        }

        return cookies;
    }

    public String get(String key) {
        return cookies.get(key);
    }
}
