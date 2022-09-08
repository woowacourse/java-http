package nextstep.jwp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {

    private final Map<String, String> cookies;
    public static final String ID = "JSESSIONID";

    public HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final String cookie) {
        return new HttpCookie(parseCookie(cookie));
    }

    private static Map<String, String> parseCookie(final String rowCookieData) {
        final Map<String, String> cookies = new HashMap<>();
        for (final String cookie : rowCookieData.split("; ")) {
            final String[] keyAndValue = cookie.split("=");
            cookies.put(keyAndValue[0], keyAndValue[1]);
        }
        return cookies;
    }

    public static HttpCookie create() {
        return new HttpCookie(Map.of(ID, UUID.randomUUID().toString()));
    }

    public boolean hasJSessionId() {
        return cookies.entrySet().stream()
                .anyMatch(it -> it.getKey().equalsIgnoreCase(ID));
    }

//    public boolean hasValue() {
//        return !value.equalsIgnoreCase("empty");
//    }

//    public String parseToHttpMessage() {
//        return "Set-Cookie: " + ID + "=" + value;
//    }
}
