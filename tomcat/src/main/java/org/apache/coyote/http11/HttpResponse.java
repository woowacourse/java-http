package org.apache.coyote.http11;

public class HttpResponse {
    private final String header;
    private final String body;

    public HttpResponse(String header, String body) {
        this.header = header;
        this.body = body;
    }
}
