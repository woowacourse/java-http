package nextstep.jwp.http.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpCookie {
    private final Map<String,String> cookies;

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(Map<String, String> headerLine) {
        final String rawStringCookies = headerLine.get(HttpHeaderType.COOKIE.value());
        final String[] rawCookies =  rawStringCookies.split("; ");
        final Map<String, String> cookies = new HashMap<>();
        for (String rawCookie : rawCookies) {
            String[] rawCookieParts = rawCookie.split("=");
            cookies.put(rawCookieParts[0], rawCookieParts[1]);
        }
        return new HttpCookie(cookies);
    }

    public Optional<String> valueFromKey(String key) {
        return Optional.ofNullable(cookies.getOrDefault(key, null));
    }
}
