package org.apache.coyote.http11.response;

public record HttpResponseStatusLine(
        String httpVersion,
        int statusCode,
        String reasonPhrase) {

    public HttpResponseStatusLine(int statusCode, String reasonPhrase) {
        this("HTTP/1.1", statusCode, reasonPhrase);
    }

    @Override
    public String toString() {
        return httpVersion + " " + statusCode + " " + reasonPhrase;
    }
}
