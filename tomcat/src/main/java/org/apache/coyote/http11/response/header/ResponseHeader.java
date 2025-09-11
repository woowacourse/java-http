package org.apache.coyote.http11.response.header;

import org.apache.coyote.http11.response.body.ResponseBody;

public class ResponseHeader {

    private final String key;
    private final String value;

    public static ResponseHeader createContentTypeHeader(final ResponseBody responseBody) {
        return new ResponseHeader("Content-Type", responseBody.getContentMimeType().getMimeType());
    }

    public static ResponseHeader createContentLength(final ResponseBody responseBody) {
        return new ResponseHeader("Content-Length", String.valueOf(responseBody.getLength()));
    }

    public static ResponseHeader createLocationHeader(final String location) {
        return new ResponseHeader("Location", location);
    }

    public static ResponseHeader createSetCookieHeader(final String cookie) {
        return new ResponseHeader("Set-Cookie", cookie);
    }

    public String toResponseText() {
        return key + ": " + value;
    }

    private ResponseHeader(final String key, final String value) {
        this.key = key;
        this.value = value;
    }
}
