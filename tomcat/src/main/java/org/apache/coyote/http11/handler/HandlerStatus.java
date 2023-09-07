package org.apache.coyote.http11.handler;

import java.util.Objects;
import org.apache.coyote.http11.message.request.RequestLine;

public class HandlerStatus {
    private final String httpMethod;
    private final String path;

    public HandlerStatus(final String httpMethod, final String path) {
        this.httpMethod = httpMethod;
        this.path = path;
    }

    public static HandlerStatus from(final RequestLine requestLine) {
        return new HandlerStatus(requestLine.getHttpMethod(), requestLine.getPath());
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HandlerStatus)) {
            return false;
        }
        final HandlerStatus that = (HandlerStatus) o;
        return Objects.equals(httpMethod, that.httpMethod) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, path);
    }
}
