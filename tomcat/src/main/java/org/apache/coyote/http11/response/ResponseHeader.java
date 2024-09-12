package org.apache.coyote.http11.response;

public class ResponseHeader {

    private static final String FORMAT_OF_HEADER = "%s: %s ";

    private final String key;
    private final String value;

    public ResponseHeader(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String buildHttpMessage() {
        return String.format(FORMAT_OF_HEADER, key, value);
    }
}
