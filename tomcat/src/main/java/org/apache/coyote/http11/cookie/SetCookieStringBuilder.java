package org.apache.coyote.http11.cookie;

public class SetCookieStringBuilder {

    private static final String OPTION_SEPARATOR = "; ";

    private final StringBuilder builder;
    private int maxAge = -1; // 브라우져 종료시 삭제
    private String domain;
    private String path;
    private boolean secure = false;
    private boolean httpOnly = false;

    public SetCookieStringBuilder(final Cookie cookie) {
        this.builder = new StringBuilder(cookie.getKey() + "=" + cookie.getValue());
    }

    public void setMaxAge(final int maxAge) {
        this.maxAge = maxAge;
    }

    public void setDomain(final String domain) {
        this.domain = domain;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public void setSecure(final boolean secure) {
        this.secure = secure;
    }

    public void setHttpOnly(final boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    @Override
    public String toString() {
        builder.append(OPTION_SEPARATOR).append("Max-Age=").append(maxAge);
        if (domain != null) {
            builder.append(OPTION_SEPARATOR).append("Domain=").append(domain);
        }
        if (path != null) {
            builder.append(OPTION_SEPARATOR).append("Path=").append(path);
        }
        if (secure) {
            builder.append(OPTION_SEPARATOR).append("Secure");
        }
        if (httpOnly) {
            builder.append(OPTION_SEPARATOR).append("HttpOnly");
        }
        return builder.toString();
    }
}
