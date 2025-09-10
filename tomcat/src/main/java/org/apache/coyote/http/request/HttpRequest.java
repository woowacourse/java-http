package org.apache.coyote.http.request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http.cookie.HttpCookie;

public class HttpRequest {

    private final String method;
    private final String endpoint;
    private final Map<String, String> headers;
    private final String body;
    private final HttpCookie cookies;
    private final SessionManager sessionManager;

    public HttpRequest(String method, String endpoint, Map<String, String> headers, String body, SessionManager sessionManager) {
        this.method = method;
        this.endpoint = endpoint;
        this.headers = Map.copyOf(headers);
        this.body = body;
        this.cookies = HttpCookie.parse(headers.get("Cookie"));
        this.sessionManager = sessionManager;
    }

    public Map<String, String> parseFormData() {
        if (!hasBody()) {
            return Map.of();
        }

        Map<String, String> params = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return Map.copyOf(params);
    }

    public String getMethod() {
        return method;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getBody() {
        return body;
    }

    public boolean hasBody() {
        return !body.isEmpty();
    }

    public HttpCookie getCookies() {
        return cookies;
    }

    public Session getSession(boolean create) {
        String sessionId = cookies.getJSessionId();

        if (sessionId != null) {
            try {
                Session session = sessionManager.findSession(sessionId);
                if (session != null) {
                    return session;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (create) {
            return sessionManager.createSession();
        }

        return null;
    }

    public Session getSession() {
        return getSession(true); // 기본값은 true
    }
}
