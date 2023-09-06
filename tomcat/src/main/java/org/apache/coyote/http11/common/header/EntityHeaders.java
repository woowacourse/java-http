package org.apache.coyote.http11.common.header;

import static org.apache.coyote.http11.common.header.HeaderName.CONTENT_LENGTH;
import static org.apache.coyote.http11.common.header.HeaderName.CONTENT_TYPE;

import java.util.Map;
import org.apache.coyote.http11.common.ContentType;

public class EntityHeaders extends Headers {

    public EntityHeaders(final Map<HeaderName, String> headers) {
        super(headers);
    }

    @Override
    public boolean isType(final HeaderName headerName) {
        return HeaderName.isEntityHeaders(headerName) || HeaderName.isNotDefined(headerName);
    }

    public void addContentType(final String contentTypeString) {
        ContentType.validate(contentTypeString);

        final var old = getContentType();
        if (old == null) {
            add(CONTENT_TYPE, contentTypeString);
            return;
        }
        add(CONTENT_TYPE, String.join(",", old, contentTypeString));
    }

    public boolean hasContentLength() {
        return contains(CONTENT_LENGTH);
    }

    public String getContentLength() {
        return find(CONTENT_LENGTH);
    }

    public String getContentType() {
        final var contentTypeStrings = find(CONTENT_TYPE).split(",");
        return contentTypeStrings[0];
    }

}
