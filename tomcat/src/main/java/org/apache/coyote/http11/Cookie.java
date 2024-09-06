package org.apache.coyote.http11;

import java.util.UUID;

public class Cookie {

    private final String jsessionid;

    public Cookie(final String id) {
        jsessionid = "JSESSIONID=" + id;
    }

    public String getJsessionid() {
        return jsessionid;
    }
}
