package org.apache.coyote.http11.session;

import org.apache.coyote.http11.response.Cookie;

public class Cookies {

    public static final String JSESSIONID = "JSESSIONID";

    public static Cookie ofJSessionId(String id) {
        Cookie cookie = new Cookie();
        cookie.addCookie(JSESSIONID, id);
        return cookie;
    }
}
