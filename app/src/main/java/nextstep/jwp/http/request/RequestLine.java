package nextstep.jwp.http.request;

public class RequestLine {

    private final HttpMethod method;
    private final RequestUri requestUri;
    private final RequestProtocol requestProtocol;

    public RequestLine(String[] line) {
        this(HttpMethod.from(line[0]), new RequestUri(line[1]), new RequestProtocol(line[2]));
    }

    public RequestLine(HttpMethod method, RequestUri requestUri, RequestProtocol requestProtocol) {
        this.method = method;
        this.requestUri = requestUri;
        this.requestProtocol = requestProtocol;
    }

    public String getPath() {
        return requestUri.getPath();
    }

    public String getUri() {
        return requestUri.getUri();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public RequestUri getRequestUri() {
        return requestUri;
    }

    public RequestProtocol getRequestProtocol() {
        return requestProtocol;
    }
}
