package org.apache.coyote.http11.request.line;

import java.util.Arrays;
import java.util.List;

public class RequestLine {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int PROTOCOL_INDEX = 2;

    private final HttpMethod method;
    private final Path path;
    private final Protocol protocol;

    private RequestLine(HttpMethod method, Path path, Protocol protocol) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
    }

    public static RequestLine from(String requestLine) {
        List<String> requestLineElements = Arrays.asList(requestLine.split(" "));
        HttpMethod method = HttpMethod.from(requestLineElements.get(HTTP_METHOD_INDEX));
        Path path = Path.from(requestLineElements.get(PATH_INDEX));
        Protocol protocol = Protocol.from(requestLineElements.get(PROTOCOL_INDEX));
        return new RequestLine(method, path, protocol);
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
