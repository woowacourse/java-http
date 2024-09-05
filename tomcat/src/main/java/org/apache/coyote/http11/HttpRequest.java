package org.apache.coyote.http11;

import java.net.URI;

public record HttpRequest(String startLine, Header header, QueryParameter body) {

    public HttpMethod getMethod() {
        return HttpMethod.from(startLine.split(" ")[0]);
    }

    public URI getUri() {
        return URI.create(startLine.split(" ")[1]);
    }
}
