package org.apache.coyote.http11.httprequest;

public class HttpCookieConvertor {

    public static HttpCookie convertHttpCookie(String cookie) {
        HttpCookie httpCookie = new HttpCookie();
        String[] cookies = cookie.split("; ");
        for (String c : cookies) {
            if (c.split("=").length >= 2) {
                httpCookie.addCookie(c.split("=")[0], c.split("=")[1]);
            }
        }
        return httpCookie;
    }
}
