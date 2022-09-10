package org.apache.coyote.http11;

import java.util.Objects;

public class HttpRequestMapping {

    private final String url;
    private final String method;

    public HttpRequestMapping(final String url, final String method) {
        this.url = url;
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public boolean match(final HttpRequest httpRequest) {
        return getUrlExceptQueryParam(httpRequest).equals(url) && httpRequest.getMethod().equals(method);
    }

    private String getUrlExceptQueryParam(final HttpRequest httpRequest) {
        String url = httpRequest.getUrl();
        if (QueryParam.isQueryParam(url)) {
            return url.split("\\?")[0];
        }
        return httpRequest.getUrl();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HttpRequestMapping that = (HttpRequestMapping) o;
        return Objects.equals(getUrl(), that.getUrl()) && Objects.equals(getMethod(), that.getMethod());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl(), getMethod());
    }
}
