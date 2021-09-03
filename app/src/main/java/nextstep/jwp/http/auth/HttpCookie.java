package nextstep.jwp.http.auth;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class HttpCookie {
    public static final HttpCookie EMPTY = new HttpCookie(null);
    private UUID SessionId;

    public HttpCookie(UUID SessionId) {
        this.SessionId = SessionId;
    }

    public static HttpCookie StringOf(String httpCookieString) {
        List<String> strings = Arrays.stream(httpCookieString.trim().split("; ")).collect(Collectors.toList());

        Map<String, String> cookieContent = new HashMap<>();
        for (String cookieString : strings) {
            String[] split = cookieString.split("=");
            cookieContent.put(split[0], split[1]);
        }

//        List<String> sessionIds = Arrays.stream(strings.get(strings.size() - 1).split("="))
//                .collect(Collectors.toList());
        return new HttpCookie(UUID.fromString(cookieContent.get("JSESSIONID")));
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
