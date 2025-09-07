package org.apache.coyote.http11;

public class HttpResponseBody {
    private final byte[] body;
    private final MimeType mimeType;

    public HttpResponseBody(byte[] body, MimeType mimeType) {
        this.body = body;
        this.mimeType = mimeType;
    }

    public String asString() {
        return new String(body);
    }

    public byte[] getBody() {
        return body;
    }

    public int getLength() {
        return body.length;
    }

    public String getContentType() {
        return mimeType.getContentType();
    }
}
