package nextstep.jwp.http.request;

public class RequestLine {

    private final HttpMethod method;
    private final RequestUri uri;
    private final RequestProtocol protocol;

    public RequestLine(String[] line) {
        this(HttpMethod.from(line[0]), new RequestUri(line[1]), new RequestProtocol(line[2]));
    }

    public RequestLine(HttpMethod method, RequestUri uri, RequestProtocol protocol) {
        this.method = method;
        this.uri = uri;
        this.protocol = protocol;
    }

    public String getPath() {
        return uri.getPath();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public RequestUri getUri() {
        return uri;
    }

    public RequestProtocol getProtocol() {
        return protocol;
    }
}
