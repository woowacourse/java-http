package org.apache.coyote.http;

public class HttpCookie {

    private final String name;
    private final String value;

    public HttpCookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static HttpCookie ofJSessionId(String sessionId) {
        return new HttpCookie("JSESSIONID", sessionId);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }
}
