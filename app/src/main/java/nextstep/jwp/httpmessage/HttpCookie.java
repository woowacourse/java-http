package nextstep.jwp.httpmessage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpCookie {

    private Map<String, String> cookies = new LinkedHashMap<>();

    public HttpCookie() {
    }

    public HttpCookie(String cookie) {
        this.cookies = parseStringToMap(cookie);
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }

    public void setCookie(String key, String value) {
        cookies.put(key, value);
    }

    private Map<String, String> parseStringToMap(String cookie) {
        return Stream.of(cookie.split("; "))
                .map(it -> it.split("="))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));
    }

    public String toValuesString() {
        return cookies.entrySet().stream()
                .map(it -> it.getKey() + "=" + it.getValue())
                .collect(Collectors.joining("; "));
    }
}
