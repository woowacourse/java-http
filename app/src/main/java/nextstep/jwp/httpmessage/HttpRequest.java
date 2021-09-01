package nextstep.jwp.httpmessage;

import java.util.Enumeration;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;
    private final Parameters parameters;

    public HttpRequest(HttpMessageReader bufferedReader) {
        this(new RequestLine(bufferedReader.getStartLine()),
                new HttpHeaders(bufferedReader.getHeaders()),
                new Parameters(bufferedReader.getParameters()));
    }

    public HttpRequest(RequestLine requestLine, HttpHeaders httpHeaders, Parameters parameters) {
        this.requestLine = requestLine;
        this.httpHeaders = httpHeaders;
        this.parameters = parameters;
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

    public Parameters getParameters() {
        return parameters;
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
}
