package nextstep.jwp.httpmessage;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;

    public HttpRequest(HttpMessageReader bufferedReader) {
        this(new RequestLine(bufferedReader.getStartLine()), new HttpHeaders(bufferedReader.getHeaders()));
    }

    public HttpRequest(RequestLine requestLine, HttpHeaders httpHeaders) {
        this.requestLine = requestLine;
        this.httpHeaders = httpHeaders;
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

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }
}
