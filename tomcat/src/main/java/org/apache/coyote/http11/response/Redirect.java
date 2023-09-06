package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.List;

public class Redirect implements Response {

    private final String location;

    public Redirect(final String location) {
        this.location = location;
    }

    @Override
    public String get() {
        final List<String> headers = new ArrayList<>();
        headers.add("HTTP/1.1 302 Found ");
        headers.add("Location: /" + location);
        headers.add("");

        return String.join("\r\n", headers);
    }
}
