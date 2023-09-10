package org.apache.catalina;

import java.io.IOException;
import java.util.UUID;
import org.apache.catalina.session.Manager;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestCookies;
import org.apache.coyote.http11.request.HttpRequestHeaders;

public class Request {

    private final HttpRequest httpRequest;
    private final HttpRequestCookies httpRequestCookies;
    private final Session session;

    public Request(HttpRequest httpRequest) throws IOException {
        this.httpRequest = httpRequest;
        this.httpRequestCookies = generateHttpCookies(httpRequest.getHttpRequestHeaders());
        this.session = generateSession();
    }

    private HttpRequestCookies generateHttpCookies(final HttpRequestHeaders httpRequestHeaders) {
        String cookies = httpRequestHeaders.getValue(HttpHeaders.COOKIE);
        if (cookies != null) {
            return HttpRequestCookies.of(cookies);
        }
        return HttpRequestCookies.empty();
    }

    private Session generateSession() throws IOException {
        Manager manager = new SessionManager();
        String jsessionId = httpRequestCookies.get("JSESSIONID");
        if (jsessionId != null && manager.findSession(jsessionId) != null) {
            return manager.findSession(jsessionId);
        }
        Session newSession = new Session(String.valueOf(UUID.randomUUID()));
        manager.add(newSession);
        return newSession;
    }

    public String getRequestBody() {
        return httpRequest.getRequestBody();
    }

    public String getPath() {
        return httpRequest.getHttpRequestStartLine()
                .getUri()
                .getPath();
    }

    public Session getSession() {
        return session;
    }
}
