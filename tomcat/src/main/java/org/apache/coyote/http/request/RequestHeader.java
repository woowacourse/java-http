package org.apache.coyote.http.request;

import org.apache.coyote.http.Header;
import org.apache.coyote.http.HttpCookie;

import java.util.List;

public class RequestHeader extends Header {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";

    public RequestHeader(List<String> splitHeaders) {
        super(splitHeaders);
    }

    public HttpCookie getCookie() {
        if (hasHeader(COOKIE)) {
            return HttpCookie.of(getValue(COOKIE));
        }
        return new HttpCookie();
    }

    public String getContentLength() {
        return getValue(CONTENT_LENGTH);
    }
}
