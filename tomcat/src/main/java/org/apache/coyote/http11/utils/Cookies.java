package org.apache.coyote.http11.utils;

public class Cookies {

    private Cookies() {
    }

    public static String ofJSessionId(String id) {
        return "JSESSIONID=" + id;
    }
}
