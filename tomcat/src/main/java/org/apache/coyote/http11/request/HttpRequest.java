package org.apache.coyote.http11.request;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public class HttpRequest {

    private final RequestStartLine startLine;

    public HttpRequest(RequestStartLine startLine) {
        this.startLine = startLine;
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public String getPath() {
        return startLine.getPath();
    }

    public Optional<String> getQueryParameter(String key) {
        return startLine.getQueryParameter(key);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        HttpRequest request = (HttpRequest) object;
        return Objects.equals(startLine, request.startLine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startLine);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HttpRequest.class.getSimpleName() + "[", "]")
                .add("startLine=" + startLine)
                .toString();
    }
}
