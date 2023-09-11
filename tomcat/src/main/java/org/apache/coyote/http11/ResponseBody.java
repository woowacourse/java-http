package org.apache.coyote.http11;

import static org.apache.coyote.http11.ContentType.PLAINTEXT_UTF8;

public class ResponseBody {

    public static final ResponseBody EMPTY = new ResponseBody("", ContentType.PLAINTEXT_UTF8, ContentLength.EMPTY);

    private final String body;
    private final ContentType contentType;
    private final ContentLength contentLength;

    private ResponseBody(final String body, final ContentType contentType, final ContentLength contentLength) {
        this.body = body;
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    public static ResponseBody of(final ContentType contentType, final String messageBody) {
        ContentLength contentLength = ContentLength.from(messageBody);
        return new ResponseBody(messageBody, contentType, contentLength);
    }

    public static ResponseBody from(final String body) {
        ContentLength contentLength = ContentLength.from(body);
        return new ResponseBody(body, PLAINTEXT_UTF8, contentLength);
    }

    public String getBody() {
        return body;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public ContentLength getContentLength() {
        return contentLength;
    }

    public boolean isEmpty() {
        return body.isEmpty();
    }

}
