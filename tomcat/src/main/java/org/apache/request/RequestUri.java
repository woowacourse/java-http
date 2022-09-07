package org.apache.request;

import java.util.Optional;

public class RequestUri {

    public static final String STATIC_RESOURCE_TYPE_DELIMITER = ".";
    public static final String QUERY_STRING_DELIMITER = "?";

    private String requestUri;

    public RequestUri(final String requestUri) {
        this.requestUri = requestUri;
    }

    public Optional<String> findStaticResourceType() {
        if (!this.requestUri.contains(STATIC_RESOURCE_TYPE_DELIMITER)) {
            return Optional.empty();
        }
        String[] path = this.requestUri.split("\\" + STATIC_RESOURCE_TYPE_DELIMITER);
        return Optional.of(path[path.length - 1]);
    }

    public String parsePath() {
        return this.requestUri.split("\\" + QUERY_STRING_DELIMITER)[0];
    }

    public String getValue() {
        return requestUri;
    }
}
