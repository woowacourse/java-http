package org.apache.coyote.http11;

import java.net.URI;

public class RequestLine {
    private final String method;
    private final String path;
    private final String protocol_version;

    private RequestLine(String method, String path, String protocol_version) {
        this.method = method;
        this.path = path;
        this.protocol_version = protocol_version;
    }

    public static RequestLine from(String requestLine) {
        String[] requestParts = requestLine.trim().split("\\s+");

        return new RequestLine(requestParts[0], requestParts[1], requestParts[2]);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        URI uri = URI.create(path);

        return uri.getPath();
    }
}
