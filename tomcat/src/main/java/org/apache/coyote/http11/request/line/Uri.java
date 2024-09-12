package org.apache.coyote.http11.request.line;

import java.util.Objects;

public class Uri {

    private static final String HOME = "/";

    private final String path;

    public Uri(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public boolean isHome() {
        return HOME.equals(path);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Uri uri = (Uri) o;
        return Objects.equals(path, uri.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
