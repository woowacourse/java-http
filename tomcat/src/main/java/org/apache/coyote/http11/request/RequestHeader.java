package org.apache.coyote.http11.request;

public class RequestHeader {

    private static final String SEPARATOR = ": ";
    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final String name;
    private final String value;

    private RequestHeader(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public static RequestHeader from(final String request) {
        final String[] header = request.split(SEPARATOR);

        return new RequestHeader(header[NAME_INDEX], header[VALUE_INDEX]);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
