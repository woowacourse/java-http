package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.HttpRequest;

public class Http11Request implements HttpRequest {

    private static final String JSESSIONID = "JSESSIONID";

    private final Http11RequestLine requestLine;
    private final Http11RequestHeaders headers;
    private final Http11RequestBody body;

    public Http11Request(
            Http11RequestLine requestLine,
            Http11RequestHeaders headers,
            Http11RequestBody body
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    @Override
    public String getMethod() {
        return requestLine.getMethod();
    }

    @Override
    public String getRequestURI() {
        return requestLine.getURI();
    }

    @Override
    public String getPath() {
        return requestLine.getPath();
    }

    @Override
    public boolean isExistsSession() {
        return headers.existsCookie(JSESSIONID);
    }

    @Override
    public String getHeader(String header) {
        return headers.getValue(header);
    }

    @Override
    public String getCookie(String cookieName) {
        return headers.getCookie(cookieName);
    }

    @Override
    public Map<String, String> getParsedBody() {
        return body.parseBody();
    }

    @Override
    public Session getSession() {
        Optional<Session> sessionOptional = SessionManager.getInstance().findSession(getCookie(JSESSIONID));
        return sessionOptional.orElseGet(() -> new Session(UUID.randomUUID().toString()));
    }

}
