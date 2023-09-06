package org.apache.coyote.httpresponse.header;

import org.apache.coyote.httpresponse.ContentBody;

public class ContentLengthHeader implements ResponseHeader {

    private static final String DELIMITER = ": ";

    private final ContentBody contentBody;

    public ContentLengthHeader(final ContentBody contentBody) {
        this.contentBody = contentBody;
    }

    @Override
    public String getKeyAndValue(final ResponseHeaderType headerType) {
        return headerType.getHeaderName() + DELIMITER + countBytes();
    }

    private int countBytes() {
        return contentBody.getValue().getBytes().length;
    }
}
