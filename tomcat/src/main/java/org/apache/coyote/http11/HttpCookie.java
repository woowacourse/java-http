package org.apache.coyote.http11;

public class HttpCookie {

    private static final String KEY_VALUE_REGEX = "=";

    private String name;
    private String value;
    private boolean httpOnly;

    public HttpCookie(String name) {
        this(name, "");
    }

    public HttpCookie(String name, String value) {
        this.name = name;
        this.value = value;
        this.httpOnly = false;
    }

    public HttpCookie() {
        this("", "");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setHttpOnly(boolean isHttpOnly) {
        this.httpOnly = isHttpOnly;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(KEY_VALUE_REGEX).append(value);

        if (httpOnly) {
            sb.append("; HttpOnly");
        }
        return sb.toString();
    }
}
