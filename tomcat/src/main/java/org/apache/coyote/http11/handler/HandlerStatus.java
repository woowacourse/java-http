package org.apache.coyote.http11.handler;

import java.util.Objects;
import org.apache.coyote.http11.message.request.RequestLine;

public class HandlerStatus {
    private final String path;

    public HandlerStatus(final String path) {
        this.path = path;
    }

    public static HandlerStatus from(final RequestLine requestLine) {
        return new HandlerStatus(requestLine.getPath());
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
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
