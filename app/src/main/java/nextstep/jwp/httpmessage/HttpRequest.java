package nextstep.jwp.httpmessage;

public class HttpRequest {

    private final RequestLine requestLine;

    public HttpRequest(HttpMessageReader bufferedReader) {
        this(new RequestLine(bufferedReader.getStartLine()));
    }

    public HttpRequest(RequestLine requestLine) {
        this.requestLine = requestLine;
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
}
