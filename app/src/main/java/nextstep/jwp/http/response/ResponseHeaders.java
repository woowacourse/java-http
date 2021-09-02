package nextstep.jwp.http.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static nextstep.jwp.common.LineSeparator.NEW_LINE;

public class ResponseHeaders {

    private final Map<String, String> headersExceptCookie;
    private final ResponseCookie cookie;

    public ResponseHeaders() {
        headersExceptCookie = new ConcurrentHashMap<>();
        cookie = new ResponseCookie();
    }

    public void addExceptCookie(String key, String value) {
        headersExceptCookie.put(key, value);
    }

    public void addCookie(String key, String value) {
        cookie.add(key, value);
    }

    @Override
    public String toString() {
        final List<String> parsedHeaders = getParsedHeaders();
        return String.join(NEW_LINE, parsedHeaders);
    }

    private List<String> getParsedHeaders() {
        List<String> parsedHeaders = new ArrayList<>();
        for (Map.Entry<String, String> entry : headersExceptCookie.entrySet()) {
            parsedHeaders.add(entry.getKey() + ": " + entry.getValue() + " ");
        }
        parsedHeaders.addAll(cookie.getAsSetCookieString());
        return parsedHeaders;
    }
}
