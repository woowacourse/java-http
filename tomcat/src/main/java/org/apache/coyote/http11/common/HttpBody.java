package org.apache.coyote.http11.common;

import org.apache.coyote.http11.exception.BadRequestException;

public class HttpBody {

    private static final String EMPTY = "";

    private String body;

    private HttpBody(String body) {
        this.body = body;
    }

    public static HttpBody from(String message) {
        if (message == null) {
            throw new BadRequestException("HttpBody is Null");
        }

        return new HttpBody(message);
    }

    public static HttpBody createEmptyHttpBody() {
        return new HttpBody(EMPTY);
    }

    public void clear() {
        this.body = EMPTY;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getBytesLength() {
        return body.getBytes().length;
    }

    public String getBody() {
        return body;
    }

}
