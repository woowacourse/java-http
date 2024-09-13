package org.apache.coyote.http11.request;

import org.apache.catalina.session.Session;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.utils.Cookies;

import java.util.List;

public class RequestHeaders extends Headers {

    private static final int DEFAULT_CONTENT_LENGTH = 0;

    public RequestHeaders(List<String> headers) {
        super(headers);
    }

    public boolean hasJSessionCookie() {
        HttpHeader cookieHeader = HttpHeader.COOKIE;

        if (has(cookieHeader)) {
            return get(cookieHeader).contains(Cookies.JSESSIONID);
        }
        return false;
    }

    public Session getSession() {
        String jSessionId = getJSessionId();
        return new Session(jSessionId);
    }

    public String getJSessionId() {
        HttpCookie httpCookie = new HttpCookie(get(HttpHeader.COOKIE));
        return httpCookie.get(Cookies.JSESSIONID);
    }

    public int getContentLength() {
        String contentLength = get(HttpHeader.CONTENT_LENGTH);

        if (contentLength == null) {
            return DEFAULT_CONTENT_LENGTH;
        }
        return Integer.parseInt(contentLength);
    }
}
