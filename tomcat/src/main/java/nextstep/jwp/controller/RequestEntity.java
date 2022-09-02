package nextstep.jwp.controller;

import java.util.Objects;

public class RequestEntity {

    private static final String DEFAULT_CONTENT_TYPE = "text/html";

    private final String contentType;
    private final String uri;
    private final String queryString;

    public RequestEntity(final String contentType, final String uri, final String queryString) {
        this.contentType = convertContentType(contentType);
        this.uri = uri;
        this.queryString = queryString;
    }

    private String convertContentType(final String contentType) {
        if (Objects.isNull(contentType)) {
            return DEFAULT_CONTENT_TYPE;
        }
        return contentType;
    }

    public String getContentType() {
        return contentType;
    }

    public String getUri() {
        return uri;
    }

    public String getQueryString() {
        return queryString;
    }
}
