package nextstep.jwp.request;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.jwp.constants.HeaderType;
import nextstep.jwp.constants.HttpTerms;
import nextstep.jwp.exception.UnauthorizedException;

public class RequestHeader {
    private final Map<String, String> headers;

    private HttpCookie cookie;

    public RequestHeader(String headerLines) {
        this.headers = parseHeaders(headerLines);
    }

    private Map<String, String> parseHeaders(String lines) {
        if (lines.contains(HeaderType.COOKIE.getValue())) {
            this.cookie = new HttpCookie(headers.get(HeaderType.COOKIE.getValue()));
        }
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

    public void checkValidSessionId() {
        if (!cookie.contains(HttpTerms.JSESSIONID)) {
            throw new UnauthorizedException("인증되지 않은 사용자 입니다.");
        }
        String sessionId = cookie.get(HttpTerms.JSESSIONID);
        HttpSession session = HttpSessions.getSession(sessionId);
        Object user = session.getAttribute("user");
        if (Objects.isNull(user)) {
            throw new UnauthorizedException("인증되지 않은 사용자 입니다.");
        }

    }
}
