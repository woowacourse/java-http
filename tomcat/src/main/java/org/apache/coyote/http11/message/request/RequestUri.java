package org.apache.coyote.http11.message.request;

import java.net.URI;
import java.util.Optional;

public class RequestUri {

    private static final String FILE_EXTENSION_DELIMITER = ".";
    private static final String EMPTY_STRING = "";

    private final URI uri;

    public RequestUri(final String uri) {
        this.uri = URI.create(uri);
    }

    public Optional<String> getQuery(final String key) {
        String query = uri.getQuery();
        QueryString queryString = new QueryString(query);
        return queryString.getQuery(key);
    }

    public String getPath() {
        return uri.getPath();
    }

    public String getExtension() {
        String path = getPath();

        int lastDotIndex = path.lastIndexOf(FILE_EXTENSION_DELIMITER);
        if (lastDotIndex < 0) {
            return EMPTY_STRING;
        }

        return path.substring(lastDotIndex + 1);
    }

    public boolean hasExtension() {
        return !getExtension().isBlank();
    }
}
