package org.apache.coyote.http11.request.line;

import java.util.Arrays;
import java.util.List;

public class RequestLine {

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
         HttpMethod method = HttpMethod.from(requestLineElements.get(0));
         Path path = Path.from(requestLineElements.get(1));
         Protocol protocol = Protocol.from(requestLineElements.get(2));
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
