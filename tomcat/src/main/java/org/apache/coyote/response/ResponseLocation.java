package org.apache.coyote.response;

public class ResponseLocation {
    private final String redirectPath;

    public ResponseLocation(final String redirectPath) {
        this.redirectPath = redirectPath;
    }

    @Override
    public String toString() {
        return "Location: " + redirectPath;
    }
}
