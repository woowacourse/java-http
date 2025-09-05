package org.apache.coyote.http11;

import java.util.List;

public class HttpRequest {

    private final String URI;

    private HttpRequest(String URI) {
        this.URI = URI;
    }

    public static HttpRequest from(List<String> requestHeader) {
        String startLine = requestHeader.getFirst();
        return new HttpRequest(startLine.split(" ")[1]);
    }

    public String getURI() {
        return URI;
    }
}
