package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;
import org.apache.catalina.session.SimpleSession;

import java.io.IOException;
import java.net.URI;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final URI uri;
    private final HttpVersion httpVersion;
    private final Header header;
    private final HttpBody body;
    private final Manager sessionManager;
    private String sessionId;

    public HttpRequest(
            HttpMethod httpMethod,
            URI uri,
            HttpVersion httpVersion,
            Header header,
            HttpBody body,
            Manager sessionManager,
            String sessionId
    ) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.httpVersion = httpVersion;
        this.header = header;
        this.body = body;
        this.sessionManager = sessionManager;
        this.sessionId = sessionId;
    }

    public static HttpRequest createHttp11Message(String requestLine, Header header, HttpBody requestBody, Manager sessionManager) {
        String[] requestLines = requestLine.split(" ");
        HttpMethod httpMethod = HttpMethod.from(requestLines[0]);
        URI uri = URI.create(requestLines[1]);

        return new HttpRequest(
                httpMethod,
                uri,
                HttpVersion.HTTP_1_1,
                header,
                requestBody,
                sessionManager,
                header.getJSessionId().orElse("")
        );
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public URI getUri() {
        return uri;
    }

    public String getPath() {
        return uri.getPath();
    }

    public QueryParameter getQueryParameter() {
        return new QueryParameter(uri.getQuery());
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public Header getHeader() {
        return header;
    }

    public HttpBody getBody() {
        return body;
    }

    public HttpSession getSession(boolean sessionCreateIfAbsent) throws IOException {
        HttpSession foundSession = sessionManager.findSession(sessionId);

        if (foundSession == null && sessionCreateIfAbsent) {
            HttpSession session = new SimpleSession();
            sessionManager.add(session);
            return session;
        }

        return foundSession;
    }
}
