package org.apache.coyote.http11.request;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;
import org.apache.coyote.HttpMethod;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.Cookies;
import org.apache.coyote.http11.FormContents;
import org.apache.coyote.http11.HttpHeaders;

import java.io.IOException;
import java.util.Optional;

public class HttpRequest {
    public static final String SESSION_ID = "JSESSIONID";

    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String COOKIE_HEADER = "Cookie";

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final byte[] body;
    private final FormContents formContents;
    private final Cookies cookies;
    private final Manager manager;

    private HttpSession session;
    private boolean isNewSession = false;

    public HttpRequest(RequestLine requestLine, HttpHeaders headers, byte[] body, Manager manager) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.formContents = retrieveFormContents(headers, body);
        this.cookies = retrieveCookies(headers);
        this.manager = manager;
    }

    private static FormContents retrieveFormContents(HttpHeaders headers, byte[] body) {
        String contentTypeValue = headers.get(CONTENT_TYPE_HEADER)
                .orElse("");
        return FormContents.of(contentTypeValue, body);
    }

    private static Cookies retrieveCookies(HttpHeaders headers) {
        String cookiePairs = headers.get(COOKIE_HEADER)
                .orElse("");
        return Cookies.from(cookiePairs);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Optional<String> getParameter(String key) {
        Optional<String> queryParameter = requestLine.getUri().findParameter(key);
        if (queryParameter.isPresent()) {
            return queryParameter;
        }
        return formContents.find(key);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public Optional<HttpSession> findSession() throws IOException {
        if (session != null) {
            return Optional.of(session);
        }
        Optional<Cookie> sessionCookie = cookies.find(SESSION_ID);
        if (sessionCookie.isEmpty()) {
            return Optional.empty();
        }
        HttpSession foundSession = manager.findSession(sessionCookie.get().value());
        return Optional.ofNullable(foundSession);
    }

    public HttpSession getSession() throws IOException {
        Optional<HttpSession> foundSession = findSession();
        if (foundSession.isPresent()) {
            return foundSession.get();
        }
        session = manager.createSession();
        isNewSession = true;
        return session;
    }

    public Optional<HttpSession> createdSession() {
        if (isNewSession) {
            return Optional.of(session);
        }
        return Optional.empty();
    }

    public HttpSession renewSession() throws IOException {
        findSession().ifPresent(manager::remove);
        this.session = manager.createSession();
        isNewSession = true;
        return session;
    }
}
