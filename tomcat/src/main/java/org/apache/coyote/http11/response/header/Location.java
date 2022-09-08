package org.apache.coyote.http11.response.header;

public class Location implements HttpResponseHeader {

    private static final String HEADER_KEY = "Location: ";

    private final String value;

    public Location(String value) {
        this.value = value;
    }

    @Override
    public String toHeaderFormat() {
        return HEADER_KEY + value;
    }
}
