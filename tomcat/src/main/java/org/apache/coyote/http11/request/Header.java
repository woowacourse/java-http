package org.apache.coyote.http11.request;

public class Header {

    private final String name;
    private final String value;

    private Header(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static Header from(String header) {
        validateNotNull(header);
        String[] nameAndValue = header.split(": ");
        validateHeaderValue(nameAndValue);
        return new Header(
                nameAndValue[0],
                nameAndValue[1]
        );
    }

    private static void validateNotNull(String header) {
        if (header == null) {
            throw new InvalidHeaderException();
        }
    }

    private static void validateHeaderValue(String[] nameAndValue) {
        if (nameAndValue.length != 2) {
            throw new InvalidHeaderException();
        }
    }

    public String name() {
        return name;
    }

    public String value() {
        return value;
    }
}
