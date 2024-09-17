package org.apache.coyote.http11;

import java.util.Objects;

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

    public String getValue() {
        return value;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpCookie that = (HttpCookie) o;
        return Objects.equals(name, that.name) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
