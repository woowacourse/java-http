package org.apache.coyote.http11.response.header;

import static org.apache.coyote.http11.util.StringUtils.SPACE;

public class ContentLength implements HttpResponseHeader {

    private static final String HEADER_KEY = "Content-Length: ";

    private final int value;

    public ContentLength(int value) {
        this.value = value;
    }

    public static ContentLength from(String responseBody) {
        return new ContentLength(responseBody.getBytes().length);
    }

    @Override
    public String toHeaderFormat() {
        return HEADER_KEY + value + SPACE;
    }
}
