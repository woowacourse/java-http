package org.apache.coyote.http.cookie;

public class HttpCookie {
    private final String name;
    private final String value;

    public HttpCookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getJSessionId() {
        return value;
    }
}
