package org.apache.coyote.http11.response.header;

public class Location {

    private static final String EMPTY = "";
    private static final Location NONE = new Location(EMPTY);
    private static final String LOCATION_HEADER = "Location: ";

    public static Location None() {
        return NONE;
    }

    private final String value;

    public Location(String value) {
        this.value = value;
    }

    public String getValue() {
        return LOCATION_HEADER + value;
    }
}
