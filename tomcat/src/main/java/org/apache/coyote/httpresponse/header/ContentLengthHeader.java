package org.apache.coyote.httpresponse.header;

public class ContentLengthHeader implements ResponseHeader {

    private static final String DELIMITER = ": ";

    private final String content;

    public ContentLengthHeader(final String content) {
        this.content = content;
    }

    @Override
    public String getKeyAndValue(final ResponseHeaderType headerType) {
        return headerType.getHeaderName() + DELIMITER + countBytes();
    }

    private int countBytes() {
        return content.getBytes().length;
    }
}
