package org.apache.coyote.http11;

public class ResponseCookie {
    private final String name;
    private final String value;
    private String path;
    private String domain;
    private int maxAge = -1;
    private boolean httpOnly = false;
    private boolean secure = false;

    public ResponseCookie(
            String name,
            String value
    ) {
        this.name = name;
        this.value = value;
    }

    public ResponseCookie path(String path) {
        this.path = path;
        return this;
    }

    public ResponseCookie httpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
        return this;
    }

    public String toSetCookieHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("=").append(value);

        if (path != null) {
            sb.append("; Path=").append(path);
        }
        if (maxAge >= 0) {
            sb.append("; Max-Age=").append(maxAge);
        }
        if (httpOnly) {
            sb.append("; HttpOnly");
        }
        if (secure) {
            sb.append("; Secure");
        }

        return sb.toString();
    }
}
