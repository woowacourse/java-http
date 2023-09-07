package org.apache.coyote.http11.cookie;

import java.util.UUID;

public class HttpCookie {
    private String JSESSIONID = "";

    public HttpCookie() {
    }

    public String changeJSessionId(String jsessionId) {
       JSESSIONID = jsessionId;
       return JSESSIONID;
    }

    public String createJSession() {
        JSESSIONID = UUID.randomUUID().toString();
        return JSESSIONID;
    }

    public String getJSessionId() {
        return JSESSIONID;
    }
}
