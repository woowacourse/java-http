package org.apache.coyote.http11.cookie;

import java.util.UUID;

public class HttpCookie {
    private static String JSESSIONID = "";

    public HttpCookie() {
    }

    public void changeJSessionId(String jsessionId) {
       JSESSIONID = jsessionId;
    }

    public void createJSession() {
        JSESSIONID = UUID.randomUUID().toString();
    }

    public String getJSessionId() {
        return JSESSIONID;
    }
}
