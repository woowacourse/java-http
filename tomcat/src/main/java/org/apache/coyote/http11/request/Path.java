package org.apache.coyote.http11.request;

import java.net.URI;
import java.util.Objects;

public final class Path {
    private final URI value;

    private Path(URI value) {
        this.value = Objects.requireNonNull(value);
    }

    public static Path from(String value) {
        return new Path(URI.create(value));
    }

    public URI uri() {
        return value;
    }

    public String value() {
        return value.toString();
    }

    public String query() {
        return value.getQuery();
    }

    public String resource() {
        return value.getPath();
    }

    public boolean isSamePath(String path) {
        return value.toString().equals(path);
    }

    public boolean containsPath(String path) {
        return value.toString().contains(path);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
