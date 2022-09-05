package org.apache.coyote.http11.response.headers;

import org.apache.coyote.http11.request.headers.RequestHeader;
import org.apache.util.NumberUtil;

public class ContentLength implements ResponseHeader, RequestHeader {

    private static final long MIN_LENGTH = 0;

    private final long length;

    public ContentLength(long length) {
        this.length = Math.max(MIN_LENGTH, length);
    }

    public ContentLength(String length) {
        this(NumberUtil.parseLongSafe(length));
    }

    public static ContentLength fromBody(String bodyString) {
        return new ContentLength(bodyString.getBytes().length);
    }

    @Override
    public String getField() {
        return "Content-Length";
    }

    @Override
    public String getValue() {
        return length + "";
    }

    @Override
    public String getAsString() {
        return "Content-Length: " + length;
    }
}
