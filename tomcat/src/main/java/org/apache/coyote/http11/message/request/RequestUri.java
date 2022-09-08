package org.apache.coyote.http11.message.request;

import java.net.URI;
import java.util.Objects;
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
        QueryString queryString = QueryString.parse(query);
        return queryString.getValues(key);
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

    public boolean hasQuery() {
        String query = uri.getQuery();
        return !(Objects.isNull(query) || query.isBlank());
    }

    public boolean matches(final String uri) {
        return this.uri.getPath().equals(uri);
    }
}
