package org.apache.coyote.http11.request;

import org.apache.coyote.http11.EntityHeader;

public class RequestHeader extends EntityHeader {

    private static final String COOKIE = "Cookie";

    public boolean existsSession() {
        return headers.containsKey(COOKIE);
    }

    public int getContentLength() {
        String key = CONTENT_LENGTH;

        if (headers.containsKey(key)) {
            return Integer.parseInt(headers.get(key));
        }
        return 0;
    }

    public String getCookies() {
        return headers.get(COOKIE);
    }
}
