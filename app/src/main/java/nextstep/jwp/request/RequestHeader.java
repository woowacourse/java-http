package nextstep.jwp.request;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.jwp.constants.HeaderType;
import nextstep.jwp.constants.HttpTerms;
import nextstep.jwp.exception.HttpException;

public class RequestHeader {
    private final Map<String, String> headers;

    public RequestHeader(String headerLines) {
        this.headers = parseHeaders(headerLines);
    }

    private Map<String, String> parseHeaders(String lines) {
        return Stream.of(lines.split(HttpTerms.NEW_LINE))
                .map(line -> line.split(HttpTerms.COLUMN_SEPARATOR))
                .collect(Collectors.toMap(x -> x[HttpTerms.KEY].trim(), x -> x[HttpTerms.VALUE].trim()));
    }

    public boolean contains(String key) {
        return headers.containsKey(key);
    }

    public String get(String key) {
        return headers.get(key);
    }

    public HttpCookie getCookie() {
        if (headers.containsKey(HeaderType.COOKIE.getValue())) {
            return new HttpCookie(headers.get(HeaderType.COOKIE.getValue()));
        }
        throw new HttpException("쿠키헤더가 없습니다.");
    }
}
