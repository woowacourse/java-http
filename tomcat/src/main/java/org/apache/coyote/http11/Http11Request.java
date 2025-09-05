package org.apache.coyote.http11;

import java.util.List;

public class Http11Request {

    private final List<String> headers;

    public Http11Request(final List<String> headers) {
        this.headers = headers;
    }

    public String getUri() {
        final String firstLine = headers.getFirst();
        final String[] firstLineValues = firstLine.split(" ");

        return firstLineValues[1];
    }
}
