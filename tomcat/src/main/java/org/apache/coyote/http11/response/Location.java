package org.apache.coyote.http11.response;

public class Location {

    private final String location;

    private Location(String location) {
        this.location = location;
    }

    public static Location from(String location) {
        return new Location(location);
    }

    public String location() {
        return location;
    }

}
