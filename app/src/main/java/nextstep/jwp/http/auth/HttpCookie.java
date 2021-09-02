package nextstep.jwp.http.auth;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class HttpCookie {
    public static final HttpCookie EMPTY = new HttpCookie(null);
    private UUID SessionId;

    public HttpCookie(UUID SessionId) {
        this.SessionId = SessionId;
    }

    public static HttpCookie StringOf(String httpCookieString) {
        List<String> strings = Arrays.stream(httpCookieString.split("; ")).collect(Collectors.toList());
        List<String> sessionIds = Arrays.stream(strings.get(strings.size() - 1).split("="))
                .collect(Collectors.toList());
        return new HttpCookie(UUID.fromString(sessionIds.get(1).trim()));
    }

    public Boolean isEmpty() {
        return this.SessionId == null;
    }

    public String getSessionIdToString() {
        if (isEmpty()) {
            return null;
        }
        return SessionId.toString();
    }
}
