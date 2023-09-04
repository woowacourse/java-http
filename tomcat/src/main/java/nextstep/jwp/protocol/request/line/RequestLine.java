package nextstep.jwp.protocol.request.line;

public class RequestLine {

    private final HttpMethod method;
    private final Path path;
    private final Protocol protocol;

    public RequestLine(HttpMethod method, Path path, Protocol protocol) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
    }

    public HttpMethod method() {
        return method;
    }

    public Path path() {
        return path;
    }

    public Protocol protocol() {
        return protocol;
    }

}
