package org.apache.coyote.http11;

public class ResponseBody {

    public static final ResponseBody EMPTY = new ResponseBody("", ContentLength.EMPTY);

    private final String body;
    private final ContentLength contentLength;

    private ResponseBody(final String body, final ContentLength contentLength) {
        this.body = body;
        this.contentLength = contentLength;
    }

    public static ResponseBody from(final String body) {
        ContentLength contentLength = ContentLength.from(body);
        return new ResponseBody(body, contentLength);
    }

    public String getBody() {
        return body;
    }

    public ContentLength getContentLength() {
        return contentLength;
    }

    public boolean isEmpty() {
        return body.isEmpty();
    }

}
