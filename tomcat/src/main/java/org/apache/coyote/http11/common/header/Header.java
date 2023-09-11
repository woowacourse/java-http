package org.apache.coyote.http11.common.header;

public abstract class Header {

    protected static final String HEADER_KEY_VALUE_DELIMITER = ": ";
    protected static final String SPACE = " ";

    private final HeaderName headerName;

    protected Header(final HeaderName headerName) {
        this.headerName = headerName;
    }

    public abstract String convertToString();

    public HeaderName getHeaderProperty() {
        return headerName;
    }

    public String getHeaderPropertyName() {
        return headerName.getPropertyName();
    }
}
