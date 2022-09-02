package org.apache.coyote.http11.message.request;

import java.net.URI;
import java.util.Optional;

public class RequestUri {

    private static final String SCHEME_HOST_PORT_REGEX = "http?://.*/";
    private static final String QUERY_DELIMITER = "?";

    private final URI uri;

    public RequestUri(final String url) {
        this.uri = URI.create(removeSchemeHostPort(url));
    }

    private String removeSchemeHostPort(final String url) {
        return url.replaceAll(SCHEME_HOST_PORT_REGEX, "");
    }

    public Optional<String> getQuery(final String key) {
        String query = uri.getQuery();
        QueryString queryString = new QueryString(query);
        return queryString.getQuery(key);
    }

    public String getPathWithoutQuery() {
        return uri.getPath().split("\\?")[0];
    }

    public String getExtension() {
        String pathWithoutQuery = getPathWithoutQuery();
        int lastIndexOfSlash = pathWithoutQuery.lastIndexOf("/");
        String filename = pathWithoutQuery.substring(lastIndexOfSlash);

        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex < 0) {
            return "";
        }

        return filename.substring(lastDotIndex + 1);
    }

    public boolean hasExtension() {
        return !getExtension().isBlank();
    }
}
