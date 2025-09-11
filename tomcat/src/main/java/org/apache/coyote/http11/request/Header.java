package org.apache.coyote.http11.request;

public class Header {
    private final String header;
    private final String value;

    public Header(final String header, final String value) {
        this.header = header;
        this.value = value;
    }

    public boolean isContentLength() {
        return header.equals("Content-Length");
    }

    public boolean isCookeHeader() {
        return header.equals("Cookie");
    }

    public String getValue() {
        return value;
    }
}
