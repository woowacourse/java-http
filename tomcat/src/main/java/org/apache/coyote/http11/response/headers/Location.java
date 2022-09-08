package org.apache.coyote.http11.response.headers;

import org.apache.coyote.http11.response.PostProcessMeta;

public class Location implements ResponseHeader {

    private final String location;

    public Location(String location) {
        this.location = location;
    }

    @Override
    public String getAsString() {
        return "Location: " + location;
    }

    @Override
    public ResponseHeader postProcess(PostProcessMeta meta) {
        return this;
    }
}
