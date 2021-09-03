package nextstep.jwp.http.auth;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class HttpCookie {
    public static final HttpCookie EMPTY = new HttpCookie(new HashMap<>(), null);
    private final Map<String, String> cookies;
    private final UUID SessionId;

    public HttpCookie(Map<String, String> cookies, UUID sessionId) {
        this.cookies = cookies;
        SessionId = sessionId;
    }

    public static HttpCookie StringOf(String httpCookieString) {
        List<String> strings = Arrays.stream(httpCookieString.trim().split("; ")).collect(Collectors.toList());

        Map<String, String> cookieContent = new HashMap<>();
        for (String cookieString : strings) {
            String[] split = cookieString.split("=");
            cookieContent.put(split[0], split[1]);
        }
        return new HttpCookie(cookieContent, UUID.fromString(cookieContent.get("JSESSIONID")));
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
