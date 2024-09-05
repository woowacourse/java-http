package org.apache.coyote.http11;

import java.net.URI;

public record HttpRequest(String startLine, Header header) {

    public URI getUri() {
        return URI.create(startLine.split(" ")[1]);
    }
}
