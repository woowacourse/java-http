package org.apache.coyote.http11.response.headers;

public class Location implements ResponseHeader {

    private final String location;

    public Location(String location) {
        this.location = location;
    }

    @Override
    public String getAsString() {
        return "Location: " + location;
    }
}
