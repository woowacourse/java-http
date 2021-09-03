package nextstep.jwp.http.response;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.http.session.HttpCookie;

public class ResponseHeaders {

    private static final String SET_COOKIE = "Set-Cookie";

    private final Map<String, String> headers;

    public ResponseHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public ResponseHeaders() {
        this(new HashMap<>());
    }

    public void putHeader(String key, String value) {
        headers.put(key, value);
    }

    public String asString() {
        return headers.entrySet().stream()
            .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining("\r\n"));
    }

    public void setCookie(HttpCookie cookie) {
        headers.put(SET_COOKIE, cookie.asString());
    }
}
