package org.apache.coyote.http11.request;

public class RequestUri {

    private static final String PERIOD = ".";
    private static final String HTML_EXTENSION = ".html";
    private static final String MAIN_PAGE = "/";

    private final String uri;

    public RequestUri(String requestUri) {
        uri = resolveExtension(requestUri);
    }

    public String getUri() {
        return uri;
    }

    private String resolveExtension(String uri) {
        if (uri.contains(PERIOD) || uri.equals(MAIN_PAGE)) {
            return uri;
        }

        return uri + HTML_EXTENSION;
    }
}
