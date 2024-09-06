package org.apache.catalina.servlets.http;

public class Cookies {

    public static Cookie ofJSessionId(String sessionId) {
        return new Cookie("JSESSIONID", sessionId);
    }
}
