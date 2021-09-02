package nextstep.jwp.framework.infrastructure.http.session;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;

public class HttpSessions {

    private static final Map<String, HttpSession> httpSessions = new HashMap<>();

    private HttpSessions() {
    }

    public static void addSession(String id) {
        HttpSession httpSession = new HttpSession(id, new HashMap<>());
        httpSessions.put(id, httpSession);
    }

    public static HttpSession getSession(String id) {
        return httpSessions.get(id);
    }

    public static HttpSession getSession(HttpRequest httpRequest) {
        String id = httpRequest.getCookie().getAttribute("JSESSIONID");
        return httpSessions.get(id);
    }
}
