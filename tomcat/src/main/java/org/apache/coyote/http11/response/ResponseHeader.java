package org.apache.coyote.http11.response;

import org.apache.coyote.http11.EntityHeader;
import org.apache.coyote.http11.MimeType;

public class ResponseHeader extends EntityHeader {

    private static final String SET_COOKIE = "Set-Cookie";
    private static final String LOCATION = "Location";

    public void setContentType(MimeType contentType) {
        addHeader(CONTENT_TYPE, contentType.getType());
    }

    public void setContentLength(String contentLength) {
        addHeader(CONTENT_LENGTH, contentLength);
    }

    public void setCookie(String cookie) {
        addHeader(SET_COOKIE, cookie);
    }

    public void setLocation(String location) {
        addHeader(LOCATION, location);
    }
}
