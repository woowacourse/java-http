package org.apache.coyote.http11;

import java.net.URI;
import java.net.URISyntaxException;

public class ViewResolver {

    private static final String STATIC_FILE_PATH = "static";
    private static final String HTML_FILE_TYPE = ".html";
    private static final String ROOT_PATH = "/";
    private static final String FILE_DELIMITER = ".";
    private final String requestUrl;

    public ViewResolver(final String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public URI resolveView() throws URISyntaxException {
        if (ROOT_PATH.equals(requestUrl)) {
            return null;
        }
        if (requestUrl.contains(FILE_DELIMITER)) {
            return getClass().getClassLoader().getResource(STATIC_FILE_PATH + requestUrl).toURI();
        }
        return getClass().getClassLoader().getResource(STATIC_FILE_PATH + requestUrl + HTML_FILE_TYPE).toURI();
    }
}
