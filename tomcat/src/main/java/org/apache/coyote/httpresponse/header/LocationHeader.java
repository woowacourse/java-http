package org.apache.coyote.httpresponse.header;

public class LocationHeader implements ResponseHeader {

    private static final String DELIMITER = ": ";

    private final String path;

    public LocationHeader(final String path) {
        this.path = path;
    }

    @Override
    public String getKeyAndValue(final ResponseHeaderType headerType) {
        return headerType.getHeaderName() + DELIMITER + path;
    }
}
