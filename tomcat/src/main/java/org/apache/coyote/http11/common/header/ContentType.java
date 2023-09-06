package org.apache.coyote.http11.common.header;

import static org.apache.coyote.http11.common.header.HeaderProperty.CONTENT_TYPE;

public class ContentType extends HeaderValue {

    private final ContentTypeValue type;

    public ContentType(final ContentTypeValue type) {
        super(CONTENT_TYPE);
        this.type = type;
    }

    @Override
    public String convertToString() {
        return new StringBuilder().append(getHeaderPropertyName()).append(HEADER_KEY_VALUE_DELIMITER)
                                  .append(type.convertToString()).append(SPACE)
                                  .toString();
    }
}
