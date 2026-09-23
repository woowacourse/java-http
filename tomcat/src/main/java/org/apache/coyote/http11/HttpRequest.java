package org.apache.coyote.http11;

import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final byte[] body;
    private final FormParameters formParameters;
    private Session session;
    private boolean isSessionResolved;
    private boolean isNewSession;

    HttpRequest(
            RequestLine requestLine,
            Map<String, String> headers,
            byte[] body,
            FormParameters formParameters
    ) {
        this.requestLine = requestLine;
        this.headers = Map.copyOf(headers);
        this.body = body.clone();
        this.formParameters = formParameters;
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public String getHeader(String name) {
        return headers.get(name.toLowerCase(Locale.ROOT));
    }

    public String getBody() {
        return new String(body, StandardCharsets.UTF_8);
    }

    public String getParameter(String name) {
        return formParameters.get(name);
    }

    public Session getSession(boolean create) {
        if (!isSessionResolved) {
            Cookies cookies = new Cookies(getHeader("Cookie"));
            session = SessionManager.find(cookies.getValue(HttpCookie.SESSION_COOKIE_KEY)).orElse(null);
            isSessionResolved = true;
        }
        if (session == null && create) {
            session = SessionManager.create();
            isNewSession = true;
        }
        return session;
    }

    Optional<Session> getNewSession() {
        if (isNewSession) {
            return Optional.of(session);
        }
        return Optional.empty();
    }
}
