package com.techcourse.http.common;

public class Location {

    private static final String EMPTY_LOCATION = "";
    private static final String HTTP_HEADER_FORMAT = "Location: %s ";

    private final String value;

    public Location(final String value) {
        this.value = value;
    }

    public static Location empty() {
        return new Location(EMPTY_LOCATION);
    }

    public String toHttpHeaderFormat() {
        return String.format(HTTP_HEADER_FORMAT, value);
    }
}
