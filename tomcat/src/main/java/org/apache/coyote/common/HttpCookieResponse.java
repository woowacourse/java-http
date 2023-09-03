package org.apache.coyote.common;

import java.time.Duration;

public class HttpCookieResponse {

    private final String name;
    private final String value;
    private boolean secure;
    private boolean httpOnly;
    private Duration maxAge = Duration.ofSeconds(-1);

    public HttpCookieResponse(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public void setSecure() {
        this.secure = true;
    }

    public void setHttpOnly() {
        this.httpOnly = true;
    }

    public void setMaxAge(long seconds) {
        this.maxAge = Duration.ofSeconds(Math.max(-1, seconds));
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public boolean isSecure() {
        return secure;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public Duration getMaxAge() {
        return maxAge;
    }
}
