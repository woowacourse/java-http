package org.apache.coyote.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;

public class Request {

    private static final SessionManager sessionManager = new SessionManager();

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestParameters parameters;
    private final Cookie cookie;
    private final RequestBody body;
    private final Session session;

    public Request(RequestLine requestLine, RequestHeaders headers, RequestParameters parameters, RequestBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.cookie = parseCookie();
        this.body = body;
        this.parameters = parameters;
        this.session = parseSession();
    }

    private Cookie parseCookie() {
        String cookies = headers.getCookies();
        HashMap<String, String> collect = Arrays.stream(cookies.split("; "))
                .map(cookie -> cookie.split("=", 2))
                .filter(cookie -> cookie.length == 2)
                .collect(HashMap::new, (map, entry) -> map.put(entry[0], entry[1]), HashMap::putAll);
        return new Cookie(collect);
    }

    private Session parseSession() {
        return cookie.get("JSESSIONID").map(id -> {
            Session findSession = sessionManager.findSession(id);
            if (findSession != null) {
                return findSession;
            }
            Session session = new Session(id);
            sessionManager.add(session);
            return session;
        }).orElseGet(() -> {
            Session session = new Session(UUID.randomUUID().toString());
            sessionManager.add(session);
            return session;
        });
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public String getProtocol() {
        return requestLine.getProtocol();
    }

    public Map<String, String> getHeaders() {
        return headers.getValues();
    }

    public RequestBody getBody() {
        return body;
    }

    public Map<String, String> getParameters() {
        return parameters.getValues();
    }

    public Session getSession() {
        return session;
    }

    @Override
    public String toString() {
        return "Request{" +
               "requestLine=" + requestLine +
               ", headers=" + headers +
               ", parameters=" + parameters +
               ", cookie=" + cookie +
               ", body='" + body + '\'' +
               ", session=" + session +
               '}';
    }
}
