package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.Optional;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.MimeType;

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
    public HttpMethod getMethod() {
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
    public MimeType getAcceptMimeType() {
        return MimeType.from(getHeader("Accept").split(",")[0]);
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
        return sessionOptional.orElseGet(Session::new);
    }

}
