package nextstep.jwp.model;

import java.util.Map;
import java.util.UUID;

public class Request {

    private final RequestLine requestLine;
    private Map<String, String> headers;
    private RequestBody body;
    private Session session;

    public Request(RequestLine requestLine, Map<String, String> headers, RequestBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.session = initializeSession();
    }

    private Session initializeSession() {
        String id = initializeSessionId();
        return Sessions.getSession(id);
    }

    public String getRequestMethod() {
        return requestLine.getMethod();
    }

    public RequestBody getBody() {
        return body;
    }

    public boolean hasCookieHeader() {
        return headers.containsKey("Cookie");
    }

    public Session getSession() {
        return session;
    }

    private String initializeSessionId() {
        if (headers.containsKey("Cookie")) {
            return new Cookie(headers.get("Cookie")).getSessionId();
        }
        return UUID.randomUUID().toString();
    }

    public FileType getFileType() {
        return requestLine.getFileType();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public boolean hasQueries() {
        return requestLine.hasQueries();
    }

    public Map<String, String> getQueries() {
        return requestLine.getQueries();
    }
}
