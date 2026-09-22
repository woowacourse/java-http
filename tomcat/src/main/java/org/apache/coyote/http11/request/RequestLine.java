package org.apache.coyote.http11.request;

import org.apache.coyote.HttpMethod;

import java.util.Optional;

public class RequestLine {

    private final HttpMethod method;
    private final RequestUri uri;
    private final String version;

    public RequestLine(String value) {
        String[] parts = value.split(" ");
        this.method = HttpMethod.valueOf(parts[0]);
        this.uri = new RequestUri(parts[1]);
        this.version = parts[2];
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return uri.getPath();
    }

    public String getResourcePath() {
        return uri.getResourcePath();
    }

    public String getVersion() {
        return version;
    }

    public Optional<String> findParameter(String name) {
        return uri.findParameter(name);
    }
}
