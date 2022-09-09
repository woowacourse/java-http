package org.apache.coyote.http11.httpmessage.request;

import java.util.Objects;

public class RequestLine {

    private final HttpVersion httpVersion;
    private final HttpMethod method;
    private final HttpUri uri;

    private RequestLine(HttpMethod method, HttpUri uri, HttpVersion httpVersion) {
        this.method = method;
        this.uri = uri;
        this.httpVersion = httpVersion;
    }

    public static RequestLine of(String requestLine) {
        String[] requests = requestLine.split(" ");

        String method = requests[0];
        String uri = requests[1];
        String version = requests[2];

        return new RequestLine(
                HttpMethod.of(method),
                HttpUri.of(uri),
                HttpVersion.of(version)
        );
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public String getPath() {
        return uri.getPath();
    }

    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestLine that = (RequestLine) o;
        return method == that.method && Objects.equals(uri, that.uri) && Objects.equals(httpVersion,
                that.httpVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, uri, httpVersion);
    }
}
