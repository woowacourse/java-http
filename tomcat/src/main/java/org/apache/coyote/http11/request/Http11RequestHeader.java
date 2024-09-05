package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.cookie.Cookies;

public class Http11RequestHeader {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";

    private final Map<String, List<String>> headers;
    private final Cookies cookies;

    private Http11RequestHeader(Map<String, List<String>> headers, Cookies cookies) {
        this.headers = headers;
        this.cookies = cookies;
    }

    public static Http11RequestHeader from(Map<String, List<String>> headers) {
        Cookies cookies = Cookies.from(headers.getOrDefault(COOKIE, List.of()));
        Map<String, List<String>> values = Map.copyOf(headers);
        return new Http11RequestHeader(values, cookies);
    }

    public int getContentLength() {
        if (headers.containsKey(CONTENT_LENGTH)) {
            return Integer.parseInt(headers.get(CONTENT_LENGTH).getFirst());
        }
        return 0;
    }

    public Optional<String> getCookieValue(String key) {
        return cookies.getValue(key);
    }

    public Map<String, List<String>> map() {
        return headers;
    }

    public String getAccept() {
        return headers.getOrDefault("Accept", List.of("text/html"))
                .getFirst()
                .split(";")[0]
                .split(",")[0];
    }
}
