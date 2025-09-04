package org.apache.http;

import java.util.List;

public class HttpRequestMessage {

    private final HttpMethod method;
    private final String uri;
    private final HttpVersion version;

    public HttpRequestMessage(List<String> message) {
        List<String> startLine = List.of(message.getFirst().split(" "));
        method = HttpMethod.valueOf(startLine.get(0));
        uri = startLine.get(1);
        version = HttpVersion.parse(startLine.get(2));
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public HttpVersion getVersion() {
        return version;
    }
}
