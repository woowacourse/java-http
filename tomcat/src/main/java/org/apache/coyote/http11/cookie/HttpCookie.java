package org.apache.coyote.http11.cookie;

public class HttpCookie {

    private final String key;
    private final String value;

    public HttpCookie(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static HttpCookie jSessionId(String id) {
        return new HttpCookie("JSESSIONID", id);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + "=" + value + "; ";
    }
}
