package org.apache.coyote.http11.utils;

public class Cookies {

    public static final String JSESSIONID = "JSESSIONID";

    private Cookies() {
    }

    public static String ofJSessionId(String id) {
        return JSESSIONID + "=" + id;
    }
}
