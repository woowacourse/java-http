package org.apache.coyote.http11;

import java.util.Objects;

public class HttpUri {
    private String uri;

    public HttpUri(String uri) {
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
        final HttpUri httpUri = (HttpUri) o;
        return Objects.equals(uri, httpUri.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }

}
