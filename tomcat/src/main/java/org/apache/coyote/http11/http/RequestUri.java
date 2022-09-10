package org.apache.coyote.http11.http;

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
}
