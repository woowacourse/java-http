package org.apache.coyote.http11.message.request;

import java.util.Objects;

public class RequestMapperKey {

    private final HttpMethod method;
    private final String urlPath;

    public RequestMapperKey(HttpMethod method, String urlPath) {
        this.method = method;
        this.urlPath = urlPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestMapperKey that = (RequestMapperKey) o;
        return method == that.method && Objects.equals(urlPath, that.urlPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, urlPath);
    }
}
