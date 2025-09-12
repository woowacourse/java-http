package org.apache.coyote.http.response;

public class ResponseLine {

    private final String protocol;
    private final int statusCode;
    private final String statusText;

    public ResponseLine(final String protocol, final int statusCode, final String statusText) {
        this.protocol = protocol;
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public static ResponseLine ok() {
        return new ResponseLine("HTTP/1.1", 200, "OK");
    }

    public static ResponseLine found() {
        return new ResponseLine("HTTP/1.1", 302, "Found");
    }

    public static ResponseLine unauthorized() {
        return new ResponseLine("HTTP/1.1", 401, "Unauthorized");
    }

    @Override
    public String toString() {
        return protocol + " " + statusCode + " " + statusText;
    }
}
