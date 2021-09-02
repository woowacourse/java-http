package nextstep.jwp.server.http.common;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> httpCookie;

    public HttpCookie() {
        this.httpCookie = new LinkedHashMap<>();
    }

    public HttpCookie(String cookies) {
        this.httpCookie = parseCookies(cookies);
    }

    private Map<String, String> parseCookies(String cookies) {
        String[] split = cookies.split(";");
        return Arrays.stream(split)
                .map(cookie -> cookie.split("=", 2))
                .collect(Collectors.toMap(splitCookie -> splitCookie[0].trim(),
                        splitCookie -> splitCookie[1].trim()));
    }

    public Map<String, String> getCookie() {
        return httpCookie;
    }

    public String convertString() {
        return httpCookie.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }

    public void addCookie(String name, String value) {
        httpCookie.put(name, value);
    }
}
