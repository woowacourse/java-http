package org.apache.coyote.http11.http;

import java.util.Objects;
import org.apache.coyote.util.RequestContentTypeUtils;

public class HttpPath {

    private static final String QUERY_PARAMETER = "?";
    private static final String DEFAULT_PATH = "/";
    private static final int DOMAIN = 0;

    private final String value;

    public HttpPath(final String uri) {
        this.value = uri;
    }

    public static HttpPath of(final String uri) {
        return new HttpPath(uri);
    }

    public boolean isQuery() {
        return value.contains(QUERY_PARAMETER);
    }

    public boolean isDefault() {
        return value.equals(DEFAULT_PATH);
    }

    public boolean isDefaultResource() {
        return RequestContentTypeUtils.isDefault(value);
    }

    public ContentType getContentType() {
        return RequestContentTypeUtils.find(value);
    }

    public String getDomainPath() {
        return value.split("\\.")[DOMAIN];
    }

    public String getValue() {
        return value;
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
