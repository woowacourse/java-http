package org.apache.coyote.http11;

public class HttpMessageBody {

    private final String body;

    public HttpMessageBody(final String body) {
        this.body = body;
    }

    public static HttpMessageBody createEmptyBody() {
        return new HttpMessageBody("");
    }

    public String getBody() {
        return body;
    }

    public byte[] getBytes() {
        return body.getBytes();
    }

    public String resolveBodyMessage() {
        return body;
    }
}
