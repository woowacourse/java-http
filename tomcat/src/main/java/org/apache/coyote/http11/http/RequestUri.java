package org.apache.coyote.http11.http;

import java.util.Objects;

public class RequestUri {

    private final String requestUri;

    private RequestUri(final String requestURI) {
        this.requestUri = requestURI;
    }

    public static RequestUri from(final String requestURI) {
        if ("/".equals(requestURI)) {
            return new RequestUri("/index.html");
        }
        return new RequestUri(requestURI);
    }

    public boolean containUrl(final String url) {
        return requestUri.contains(url);
    }

    public String getRequestUri() {
        return requestUri;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RequestUri that = (RequestUri) o;
        return Objects.equals(requestUri, that.requestUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestUri);
    }
}
