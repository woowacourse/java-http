package nextstep.jwp.protocol.request_line;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLine {

    private static final Logger log = LoggerFactory.getLogger(RequestLine.class);

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
