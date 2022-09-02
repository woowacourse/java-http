package org.apache.coyote.http11;

import org.apache.catalina.Session;

public class LoginResult {

    private final String redirectUrl;
    private final Session session;

    public LoginResult(String redirectUrl, Session session) {
        this.redirectUrl = redirectUrl;
        this.session = session;
    }

    public LoginResult(String redirectUrl) {
        this(redirectUrl, null);
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public Session getSession() {
        return session;
    }
}
