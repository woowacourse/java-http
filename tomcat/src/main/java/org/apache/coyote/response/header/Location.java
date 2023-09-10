package org.apache.coyote.response.header;

public class Location {
    private final String redirectPath;

    public Location(final String redirectPath) {
        this.redirectPath = redirectPath;
    }

    @Override
    public String toString() {
        return "Location: " + redirectPath;
    }
}
