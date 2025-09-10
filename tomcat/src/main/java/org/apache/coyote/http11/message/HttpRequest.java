package org.apache.coyote.http11.message;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.cookie.RequestCookie;
import org.apache.catalina.session.SessionManager;

public class HttpRequest {

    public static final String JAVA_SESSION_ID_KEY = "JSESSIONID";

    private final RequestLine requestLine;
    private final HttpRequestHeader header;
    private final String requestBody;

    public HttpRequest(RequestLine requestLine, HttpRequestHeader header, String requestBody) {
        this.requestLine = requestLine;
        this.header = header;
        this.requestBody = requestBody;
    }

    public HttpSession getSession(SessionManager sessionManager, boolean create) {
        String jSessionId = extractSessionIdFromCookie();

        if (jSessionId != null) {
            HttpSession existingSession = sessionManager.findSession(jSessionId);
            if (existingSession != null) {
                return existingSession;
            }
        }

        if (create) {
            return sessionManager.createSession();
        }

        return null;
    }

    private String extractSessionIdFromCookie() {
        if (header.hasCookie()) {
            RequestCookie cookie = header.getCookie();
            if (cookie.contains(JAVA_SESSION_ID_KEY)) {
                return cookie.findByKey(JAVA_SESSION_ID_KEY);
            }
        }
        return null;
    }

    public HttpMethod getHttpMethod() {
        return this.requestLine.httpMethod();
    }

    public String getPath() {
        return this.requestLine.path();
    }

    public String getRequestBody() {
        return this.requestBody;
    }

    public String getHttpVersion() {
        return this.requestLine.httpVersion();
    }

    public Map<String, String> parseToMap() {
        Map<String, String> parsedRequestBody = new HashMap<>();

        String[] pairs = requestBody.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            String key = keyValue[0];
            String value = keyValue[1];
            parsedRequestBody.put(key, value);
        }

        return parsedRequestBody;
    }
}
