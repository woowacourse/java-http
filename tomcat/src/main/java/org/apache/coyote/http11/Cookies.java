package org.apache.coyote.http11;

public class Cookies {

    public static String ofJSessionId(final String sessionId) {
        return "JSESSIONID=" + sessionId;
    }
}
