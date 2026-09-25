package org.apache.coyote.http11.response;

public class HttpResponseLine {

    private final String version;
    private final int statusCode;
    private final String reasonPhrase;

    private HttpResponseLine(String version, int statusCode, String reasonPhrase) {
        this.version = version;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public static HttpResponseLine of(String version, int statusCode, String reasonPhrase) {
        return new HttpResponseLine(version, statusCode, reasonPhrase);
    }

    public String serialize() {
        return String.format("%s %d %s", version, statusCode, reasonPhrase);
    }
}
