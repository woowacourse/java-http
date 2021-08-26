package nextstep.jwp.http.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class RequestLine {

    private static final Logger LOG = LoggerFactory.getLogger(RequestLine.class);

    private final HttpMethod method;
    private final UriPath path;
    private final String protocolVersion;

    public RequestLine(String requestLine) {
        requestLine = URLDecoder.decode(requestLine, StandardCharsets.UTF_8);
        final String[] splitRequestLine = requestLine.split(" ");
        method = HttpMethod.valueOf(splitRequestLine[0]);
        path = new UriPath(splitRequestLine[1]);
        protocolVersion = splitRequestLine[2];
        LOG.debug("RequestLine => method: {}, protocolVersion: {}", method, protocolVersion);
    }

    public String getUri() {
        return path.getUri();
    }

    public boolean hasMethod(HttpMethod method) {
        return this.method == method;
    }
}
