package org.apache.coyote.http11.response;

public class HttpResponseStatusLine {

    private final String version;
    private final HttpStatus status;

    public HttpResponseStatusLine(HttpStatus status) {
        this.version = "HTTP/1.1";
        this.status = status;
    }

    public String toResponseString() {
        return version + " " + status.toResponseString();
    }
}
