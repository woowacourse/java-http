package nextstep.jwp.webserver.request;

import java.util.UUID;
import nextstep.jwp.webserver.response.HttpResponse;

public class SessionUtil {

    public static final String SESSION_ID_KEY = "JSESSIONID";

    private final HttpSessions httpSessions;
    private final HttpResponse httpResponse;

    public SessionUtil(HttpSessions httpSessions, HttpResponse httpResponse) {
        this.httpSessions = httpSessions;
        this.httpResponse = httpResponse;
    }
    
    public HttpSession createSession() {
        String sessionId = UUID.randomUUID().toString();
        httpResponse.addHeader("Set-Cookie", SESSION_ID_KEY + "=" + sessionId);
        return httpSessions.getSession(sessionId);
    }

    public HttpSession getSession(String sessionId) {
        return httpSessions.getSession(sessionId);
    }
}
