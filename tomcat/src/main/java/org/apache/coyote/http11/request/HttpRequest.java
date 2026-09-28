package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.coyote.http11.Session;

public class HttpRequest {
    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final Map<String, String> body;
    private final Session session;

    public HttpRequest(HttpRequestData requestData, Session session) {
        this.requestLine = requestData.requestLine();
        this.headers = requestData.headers();
        this.body = Map.copyOf(requestData.body());
        this.session = session;
    }

    public String getHeader(String headerName) {
        return headers.get(headerName);
    }

    public boolean isMethod(HttpMethod method) {
        return requestLine.method() == method;
    }

    public boolean matchesPath(String path) {
        return requestLine.matchPath(path);
    }

    public String getBodyValue(String name) {
        return body.getOrDefault(name, null);
    }

    public String getPath() {
        return requestLine.path();
    }

    public Session getSession() {
        return session;
    }
}
