package org.apache.coyote.http11.request;

import java.util.Objects;

public class Uri {

    private final String uri;

    public Uri(final String uri) {
        this.uri = uri;
    }

    public String getPath() {
        return uri;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Uri uri1 = (Uri) o;
        return Objects.equals(uri, uri1.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }
}
