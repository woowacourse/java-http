package org.apache.coyote.response;

public class Location {

    private final String location;

    private Location(String location) {
        this.location = location;
    }

    public static Location from(String location) {
        return new Location(location);
    }

    @Override
    public String toString() {
        return location;
    }
}
