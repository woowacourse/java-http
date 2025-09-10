package org.apache.catalina.cookie;

public enum HttpCookieName {

    JSESSIONID("JSESSIONID"),
    ;

    private final String value;

    HttpCookieName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
