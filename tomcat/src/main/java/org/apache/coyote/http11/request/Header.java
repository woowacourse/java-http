package org.apache.coyote.http11.request;

public class Header {

    private static final String SEPARATOR = ": ";
    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final String name;
    private final String value;

    private Header(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public static Header from(final String request) {
        final String[] header = request.split(SEPARATOR);

        return new Header(header[NAME_INDEX], header[VALUE_INDEX]);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
