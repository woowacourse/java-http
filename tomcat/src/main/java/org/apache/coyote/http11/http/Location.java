package org.apache.coyote.http11.http;

public class Location {

    private final String location;

    private Location(final String location) {
        this.location = location;
    }

    public static Location from(final String location) {
        return new Location(location);
    }

    public String getLocationLine() {
        return "Location: " + location;
    }
}
