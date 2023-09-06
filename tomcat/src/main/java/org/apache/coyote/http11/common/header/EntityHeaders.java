package org.apache.coyote.http11.common.header;

import static org.apache.coyote.http11.common.header.HeaderName.CONTENT_LENGTH;
import static org.apache.coyote.http11.common.header.HeaderName.CONTENT_TYPE;

import java.util.Map;

public class EntityHeaders extends Headers {

    public EntityHeaders(final Map<HeaderName, String> headers) {
        super(headers);
    }

    public EntityHeaders() {
    }

    @Override
    public boolean isType(final HeaderName headerName) {
        return HeaderName.isEntityHeaders(headerName) || HeaderName.isNotDefined(headerName);
    }

    public void addContentType(final String contentTypeString) {
        final var old = getContentType();
        if (old == null) {
            add(CONTENT_TYPE, contentTypeString);
            return;
        }
        add(CONTENT_TYPE, String.join(",", old, contentTypeString));
    }

    public void addContentLength(final String body) {
        final var bytes = body.getBytes();
        
        add(CONTENT_LENGTH, String.valueOf(bytes.length));
    }

    public boolean hasContentLength() {
        return contains(CONTENT_LENGTH);
    }

    public String getContentLength() {
        return find(CONTENT_LENGTH);
    }

    public String getContentType() {
        return find(CONTENT_TYPE);
    }
}
