package org.apache.catalina.controller;

import java.util.Objects;
import org.apache.coyote.http11.message.request.RequestLine;

public class ControllerStatus {
    private final String path;

    public ControllerStatus(final String path) {
        this.path = path;
    }

    public static ControllerStatus from(final RequestLine requestLine) {
        return new ControllerStatus(requestLine.getPath());
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ControllerStatus)) {
            return false;
        }
        final ControllerStatus that = (ControllerStatus) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
