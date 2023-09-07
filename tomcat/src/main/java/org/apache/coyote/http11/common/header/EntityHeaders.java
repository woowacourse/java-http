package org.apache.coyote.http11.common.header;

import java.util.Map;

public class EntityHeaders extends Headers {

    public EntityHeaders(final Map<String, String> headers) {
        super(headers);
    }

    public EntityHeaders() {
    }

    @Override
    public boolean isType(final String headerName) {
        return HeaderName.isEntityHeaders(headerName) || HeaderName.isNotDefined(headerName);
    }

    public void addContentType(final String contentTypeString) {
        final var old = getContentType();
        if (old == null) {
            add(HeaderName.CONTENT_TYPE, contentTypeString);
            return;
        }
        add(HeaderName.CONTENT_TYPE, String.join(",", old, contentTypeString));
    }

    public void addContentLength(final String body) {
        final var bytes = body.getBytes();

        add(HeaderName.CONTENT_LENGTH, String.valueOf(bytes.length));
    }

    public boolean hasContentLength() {
        return contains(HeaderName.CONTENT_LENGTH);
    }

    public String getContentLength() {
        return find(HeaderName.CONTENT_LENGTH);
    }

    public String getContentType() {
        return find(HeaderName.CONTENT_TYPE);
    }
}
