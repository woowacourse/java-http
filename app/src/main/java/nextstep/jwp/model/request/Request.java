package nextstep.jwp.model.request;

import java.util.Map;
import nextstep.jwp.model.FileType;
import nextstep.jwp.model.ProtocolType;

public class Request {

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestBody body;
    private final Session session;

    public Request(RequestLine requestLine, RequestHeaders headers, RequestBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.session = establishSession();
    }

    private Session establishSession() {
        Cookie cookie = headers.getCookie();
        return Sessions.getSession(cookie);
    }

    public String getRequestMethod() {
        return requestLine.getMethod();
    }

    public RequestBody getBody() {
        return body;
    }

    public boolean hasCookie() {
        return headers.hasCookie();
    }

    public Session getSession() {
        return session;
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

    public ProtocolType getProtocol() {
        return requestLine.getProtocol();
    }
}
