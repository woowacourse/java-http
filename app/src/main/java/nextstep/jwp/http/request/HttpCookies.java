package nextstep.jwp.http.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpCookies {

    private static final String COOKIES_SEPARATOR = "; ";
    private static final String COOKIE_KEY_VALUE_SEPARATOR = "=";
    private static final int KEY_ON_KEY_VALUE_FORMAT = 0;
    private static final int VALUE_ON_KEY_VALUE_FORMAT = 1;
    private final Map<String,String> cookies;

    public HttpCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookies from(Map<String, String> headerLine) {
        final String rawStringCookies = headerLine.get(HttpHeaderType.COOKIE.value());
        final String[] rawCookies =  rawStringCookies.split(COOKIES_SEPARATOR);
        final Map<String, String> cookies = new HashMap<>();
        for (String rawCookie : rawCookies) {
            String[] rawCookieParts = rawCookie.split(COOKIE_KEY_VALUE_SEPARATOR);
            cookies.put(rawCookieParts[KEY_ON_KEY_VALUE_FORMAT], rawCookieParts[VALUE_ON_KEY_VALUE_FORMAT]);
        }
        return new HttpCookies(cookies);
    }

    public Optional<String> valueFromKey(String key) {
        return Optional.ofNullable(cookies.get(key));
    }
}
