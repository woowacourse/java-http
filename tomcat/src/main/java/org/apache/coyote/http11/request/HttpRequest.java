package org.apache.coyote.http11.request;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.catalina.session.SessionGenerator;
import org.apache.catalina.session.SessionManager;

public class HttpRequest {

    private final static SessionManager SESSION_MANAGER = new SessionManager();

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpSession session;
    private final String body;

    public HttpRequest(RequestLine requestLine, HttpHeaders headers, HttpSession session, String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.session = session;
        this.body = body;
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Queries getQueries() {
        return requestLine.getQueries();
    }

    public boolean isQueriesEmpty() {
        return getQueries().isEmpty();
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getBody() {
        return body;
    }

    public String getQuery(String key) {
        return requestLine.getQuery(key);
    }

    public Set<String> getQueryKeys() {
        return requestLine.getQueryKeys();
    }

    public HttpSession getSession() {
        return session;
    }

    public static class Builder {
        private final SessionGenerator sessionGenerator;
        private RequestLine requestLine;
        private HttpHeaders headers;
        private HttpSession session;
        private String body = "";

        public Builder(SessionGenerator sessionGenerator) {
            this.sessionGenerator = sessionGenerator;
        }

        public Builder requestHead(List<String> requestHead) {
            validateRequestHead(requestHead);
            this.requestLine = getRequestLine(requestHead);
            this.headers = getHeaders(requestHead);
            this.session = getSession();
            return this;
        }

        private void validateRequestHead(List<String> requestLines) {
            if (requestLines.isEmpty()) {
                throw new IllegalArgumentException("올바르지 않은 HTTP 요청 형식입니다.");
            }
        }

        private RequestLine getRequestLine(List<String> requestHead) {
            String firstLine = requestHead.getFirst();
            return RequestLine.of(firstLine);
        }

        private HttpHeaders getHeaders(List<String> requestHead) {
            List<String> headers = new ArrayList<>(requestHead.subList(1, requestHead.size()));
            return HttpHeaders.of(headers);
        }

        private HttpSession getSession() {
            String sessionId = headers.getSessionId();
            if (sessionId == null || SESSION_MANAGER.findSession(sessionId) == null) {
                return sessionGenerator.create();
            }
            return SESSION_MANAGER.findSession(sessionId);
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public HttpRequest build() {
            if (requestLine == null || headers == null || session == null) {
                throw new IllegalArgumentException("HTTP 요청 객체를 생성할 수 없습니다.");
            }
            if (body.length() != getBodyLength()) {
                throw new IllegalArgumentException("요청 본문 길이가 올바르지 않습니다.");
            }
            return new HttpRequest(requestLine, headers, session, body);
        }

        public int getBodyLength() {
            return headers.getAsInt("Content-Length").orElse(0);
        }
    }
}
