package nextstep.jwp.http.common;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.http.common.session.HttpCookie;

public class HttpHeaders {

    private static final String COOKIE = "Cookie";
    private static final String SET_COOKIE = "Set-Cookie";

    private final Map<String, String> headers;

    private HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders of() {
        return new HttpHeaders(new LinkedHashMap<>());
    }

    public static HttpHeaders of(Map<String, String> headers) {
        return new HttpHeaders(headers);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public boolean containsKey(String key) {
        return headers.containsKey(key);
    }

    public String get(String key) {
        return headers.get(key);
    }

    public HttpCookie getCookie() {
        String rawCookie = get(COOKIE);
        return new HttpCookie(rawCookie);
    }

    public String convertToLines() {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
    }

    public void setCookie(HttpCookie cookie) {
        headers.put(SET_COOKIE, cookie.asString());
    }
}
