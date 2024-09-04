package org.apache.coyote.http11.request;

import java.util.Objects;

public class Uri {

    private final String path;

    public Uri(final String path) {
        this.path = path;
    }

    public boolean isStartsWith(Uri uri) {
        if (path.startsWith(uri.path)) {
            return true;
        }
        return false;
    }

    public String getQueryString() {
        int index = path.indexOf("?");
        return path.substring(index + 1);
    }

    public String getPath() {
        return path;
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
