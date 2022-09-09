package org.apache.coyote.http11;

public class Location {

    private static final String EMPTY_LOCATION = "";

    private final String value;

    public Location(String value) {
        this.value = value;
    }

    public static Location empty() {
        return new Location(EMPTY_LOCATION);
    }

    public boolean isEmpty() {
        return value.equals(EMPTY_LOCATION);
    }

    public String getValue() {
        return value;
    }
}
