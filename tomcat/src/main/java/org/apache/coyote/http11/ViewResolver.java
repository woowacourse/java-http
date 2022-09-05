package org.apache.coyote.http11;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class ViewResolver {

    private static final String STATIC_FILE_PATH = "static";
    private static final String HTML_FILE_TYPE = ".html";
    private static final String ROOT_PATH = "/";
    private static final String FILE_DELIMITER = ".";
    private final String requestUrl;

    public ViewResolver(final String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Optional<URI> resolveView() throws URISyntaxException {
        if (ROOT_PATH.equals(requestUrl)) {
            return Optional.empty();
        }
        if (requestUrl.contains(FILE_DELIMITER)) {
            final URI uri = getClass().getClassLoader().getResource(STATIC_FILE_PATH + requestUrl).toURI();
            return Optional.of(uri);
        }
        final URI uri = getClass().getClassLoader().getResource(STATIC_FILE_PATH + requestUrl + HTML_FILE_TYPE).toURI();
        return Optional.of(uri);
    }
}
