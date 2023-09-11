package org.apache.coyote.http11.common.header;

import static org.apache.coyote.http11.common.header.HeaderName.CONTENT_LENGTH;

public class ContentLength extends Header {

    private final int length;

    public ContentLength(final int length) {
        super(CONTENT_LENGTH);
        this.length = length;
    }

    @Override
    public String convertToString() {
        return new StringBuilder().append(getHeaderPropertyName()).append(HEADER_KEY_VALUE_DELIMITER)
                                  .append(length).append(SPACE)
                                  .toString();
    }
}
