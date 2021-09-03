package nextstep.jwp.server.http.common;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie() {
        this.cookies = new LinkedHashMap<>();
    }

    public HttpCookie(String cookies) {
        this.cookies = parseCookies(cookies);
    }

    private Map<String, String> parseCookies(String cookies) {
        String[] split = cookies.split(";");
        return Arrays.stream(split)
                .map(cookie -> cookie.split("=", 2))
                .collect(Collectors.toMap(splitCookie -> splitCookie[0].trim(),
                        splitCookie -> splitCookie[1].trim()));
    }

    public Map<String, String> getCookie() {
        return cookies;
    }

    public String convertString() {
        return cookies.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }

    public void addCookie(String name, String value) {
        cookies.put(name, value);
    }
}
