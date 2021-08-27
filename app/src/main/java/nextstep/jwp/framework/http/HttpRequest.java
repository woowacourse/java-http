package nextstep.jwp.framework.http;

public class HttpRequest {
    private RequestLine requestLine;
    private final HttpHeaders headers;
    private final StringBuilder requestBody;

    public HttpRequest(RequestLine requestLine, HttpHeaders headers, StringBuilder requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public StringBuilder getRequestBody() {
        return requestBody;
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public String getVersion() {
        return requestLine.getVersion();
    }
}
