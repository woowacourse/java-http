package org.apache.coyote.http11.cookie;

import static org.apache.coyote.http11.cookie.SetCookieOption.DOMAIN;
import static org.apache.coyote.http11.cookie.SetCookieOption.HTTP_ONLY;
import static org.apache.coyote.http11.cookie.SetCookieOption.MAX_AGE;
import static org.apache.coyote.http11.cookie.SetCookieOption.PATH;
import static org.apache.coyote.http11.cookie.SetCookieOption.SECURE;

public class SetCookieStringBuilder {

    private static final String OPTION_SEPARATOR = "; ";
    private static final String OPTION_KEY_VALUE_SEPARATOR = "=";

    private final Cookie cookie;
    private int maxAge = -1; // 브라우져 종료시 삭제
    private String domain;
    private String path;
    private boolean secure = false;
    private boolean httpOnly = false;

    public SetCookieStringBuilder(final Cookie cookie) {
        this.cookie = cookie;
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
        final StringBuilder builder = new StringBuilder(cookie.getKey() + "=" + cookie.getValue());
        setMaxAge(builder);
        setDomain(builder);
        setPath(builder);
        setSecure(builder);
        setHttpOnly(builder);

        return builder.toString();
    }

    private void setMaxAge(final StringBuilder builder) {
        builder.append(OPTION_SEPARATOR)
                .append(MAX_AGE.getOptionKey())
                .append(OPTION_KEY_VALUE_SEPARATOR)
                .append(maxAge);
    }

    private void setDomain(final StringBuilder builder) {
        if (domain != null) {
            builder.append(OPTION_SEPARATOR)
                    .append(DOMAIN.getOptionKey())
                    .append(OPTION_KEY_VALUE_SEPARATOR)
                    .append(domain);
        }
    }

    private void setPath(final StringBuilder builder) {
        if (path != null) {
            builder.append(OPTION_SEPARATOR)
                    .append(PATH.getOptionKey())
                    .append(OPTION_KEY_VALUE_SEPARATOR)
                    .append(path);
        }
    }

    private void setSecure(final StringBuilder builder) {
        if (secure) {
            builder.append(OPTION_SEPARATOR)
                    .append(SECURE.getOptionKey());
        }
    }

    private void setHttpOnly(final StringBuilder builder) {
        if (httpOnly) {
            builder.append(OPTION_SEPARATOR)
                    .append(HTTP_ONLY.getOptionKey());
        }
    }
}
