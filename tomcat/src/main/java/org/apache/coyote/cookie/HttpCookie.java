package org.apache.coyote.cookie;

import java.util.Objects;

public final class HttpCookie {

    private final String name;
    private final String value;
    private Long maxAge;
    private String domain;
    private String path;
    private boolean secure;
    private boolean httpOnly;

    public HttpCookie(String name, String value) {
        this.name = Objects.requireNonNull(name);
        this.value = Objects.requireNonNull(value);
    }

    public HttpCookie setMaxAge(long maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    public HttpCookie setPath(String path) {
        this.path = path;
        return this;
    }

    public HttpCookie setSecure(boolean secure) {
        this.secure = secure;
        return this;
    }

    public HttpCookie setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getHeaderValue() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("=").append(value);
        if (maxAge != null) {
            sb.append("; Max-Age=").append(maxAge);
        }
        if (path != null) {
            sb.append("; Path=").append(path);
        }
        if (secure) {
            sb.append("; Secure");
        }
        if (httpOnly) {
            sb.append("; HttpOnly");
        }
        return sb.toString();
    }
}