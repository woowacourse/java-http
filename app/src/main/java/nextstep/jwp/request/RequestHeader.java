package nextstep.jwp.request;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.jwp.constants.Header;
import nextstep.jwp.constants.HttpTerms;

public class RequestHeader {
    private final Map<String, String> headers;

    private HttpCookie cookie;

    public RequestHeader(String headerLines) {
        this.headers = parseHeaders(headerLines);
    }

    private Map<String, String> parseHeaders(String lines) {
        Map<String, String> headers = Stream.of(lines.split(HttpTerms.NEW_LINE))
                .map(line -> line.split(HttpTerms.COLUMN_SEPARATOR))
                .collect(Collectors.toMap(x -> x[HttpTerms.KEY].trim(), x -> x[HttpTerms.VALUE].trim()));
        if (lines.contains(Header.COOKIE.getType())) {
            this.cookie = new HttpCookie(headers.get(Header.COOKIE.getType()));
        }
        return headers;
    }

    public boolean contains(String key) {
        return headers.containsKey(key);
    }

    public String get(String key) {
        return headers.get(key);
    }

    public HttpCookie getCookie() {
        return this.cookie;
    }
}
