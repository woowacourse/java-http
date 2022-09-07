package org.apache.coyote.http11.cookie;

import java.util.Map;
import org.apache.coyote.http11.HeaderElement;
import org.apache.coyote.http11.http11request.Http11Request;

public class CookieManager {

    public void addCookieIfAbsent(Http11Request http11Request) {
        Map<String, String> header = http11Request.getHeader();
        if (!header.containsKey(HeaderElement.COOKIE.getValue())) {
            http11Request.setHeaderAttribute(HeaderElement.COOKIE.getValue(), "");
        }
    }

    public HttpCookie getCookieIfAbsentCreate(Http11Request http11Request) {
        Map<String, String> header = http11Request.getHeader();
        if (!header.containsKey(HeaderElement.COOKIE.getValue())) {
            http11Request.setHeaderAttribute(HeaderElement.COOKIE.getValue(), "");
        }
        return HttpCookie.of(http11Request.getHeaderAttribute(HeaderElement.COOKIE.getValue()));
    }
}
