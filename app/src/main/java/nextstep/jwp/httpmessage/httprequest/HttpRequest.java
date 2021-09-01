package nextstep.jwp.httpmessage.httprequest;

import nextstep.jwp.httpmessage.HttpHeaders;
import nextstep.jwp.httpmessage.HttpMethod;
import nextstep.jwp.httpmessage.HttpSession;
import nextstep.jwp.httpmessage.HttpSessions;

import java.util.Enumeration;
import java.util.Objects;
import java.util.UUID;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;
    private final Parameters parameters;

    private HttpSession httpSession;

    public HttpRequest(HttpMessageReader bufferedReader) {
        this(new RequestLine(bufferedReader.getStartLine()),
                new HttpHeaders(bufferedReader.getHeaders()),
                new Parameters(bufferedReader.getParameters())
        );
    }

    public HttpRequest(RequestLine requestLine, HttpHeaders httpHeaders, Parameters parameters) {
        this.requestLine = requestLine;
        this.httpHeaders = httpHeaders;
        this.parameters = parameters;
        this.httpSession = HttpSessions.getSession(httpHeaders.getSessionId());
    }

    public String getRequestLine() {
        return requestLine.getLine();
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getVersionOfTheProtocol() {
        return requestLine.getVersionOfTheProtocol();
    }

    public String getHeader(String name) {
        return httpHeaders.getHeader(name);
    }

    public int httpHeaderSize() {
        return httpHeaders.size();
    }

    public Enumeration<String> getParameterNames() {
        return parameters.getParameterNames();
    }

    public String getParameter(String name) {
        return parameters.getParameter(name);
    }

    public HttpSession getHttpSession() {
        if (Objects.nonNull(httpSession)) {
            return httpSession;
        }
        this.httpSession = new HttpSession(UUID.randomUUID().toString());
        return this.httpSession;
    }

    public boolean hasSession() {
        return Objects.nonNull(httpSession);
    }

    public String getHttpSessionId() {
        return httpSession.getId();
    }
}
