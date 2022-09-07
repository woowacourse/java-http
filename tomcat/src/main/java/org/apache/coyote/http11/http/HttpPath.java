package org.apache.coyote.http11.http;

import java.util.Objects;
import org.apache.coyote.util.RequestContentTypeUtils;

public class HttpPath {

    private static final String QUERY_PARAMETER = "?";

    private final String value;

    public HttpPath(final String uri) {
        this.value = uri;
    }

    public String getValue() {
        return value;
    }

    public ContentType getContentType() {
        return RequestContentTypeUtils.find(value);
    }

    public static HttpPath of(final String uri) {
        return new HttpPath(uri);
    }

    public boolean isQuery() {
        return value.contains(QUERY_PARAMETER);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HttpPath)) {
            return false;
        }
        HttpPath that = (HttpPath) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
