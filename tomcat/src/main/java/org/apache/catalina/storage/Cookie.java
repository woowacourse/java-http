package org.apache.catalina.storage;

public class Cookie {

    public static final String SESSION_COOKIE_ID = "JSESSIONID";

    private final String name;
    private final String value;
    private String path;
    private long maxAge = -1;
    private boolean secure = false;
    private boolean httpOnly = false;

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static Cookie of(final Session session) {
        final var cookie = new Cookie(SESSION_COOKIE_ID, session.getId());
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public void setMaxAge(final long maxAge) {
        this.maxAge = maxAge;
    }

    public void setSecure(final boolean secure) {
        this.secure = secure;
    }

    public void setHttpOnly(final boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(name + "=" + value);

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
