package org.apache.coyote.http11;

import java.util.Objects;

public class HttpPath {

    private String uri;

    public HttpPath(String uri) {
        this.uri = uri;
    }

    public String getValue() {
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
        final HttpPath httpPath = (HttpPath) o;
        return Objects.equals(uri, httpPath.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }

}
