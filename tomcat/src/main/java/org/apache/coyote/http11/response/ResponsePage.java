package org.apache.coyote.http11.response;

import java.util.Optional;

import org.apache.catalina.auth.HttpCookie;
import org.apache.catalina.auth.Session;
import org.apache.catalina.auth.SessionManager;

public enum ResponsePage {
    LOGIN_IS_LOGIN("/login", HttpStatus.OK, "/login.html", false),
    LOGIN_IS_NOT_LOGIN("/login", HttpStatus.OK, "/index.html", true),
    REGISTER_IS_NOT_LOGIN("/register", HttpStatus.OK, "/register.html", false),
    REGISTER_IS_LOGIN("/register", HttpStatus.OK, "/index.html", true),
    ;

    private final String url;
    private final HttpStatus status;
    private final String fileName;
    private final boolean isLogin;

    ResponsePage(String url, HttpStatus status, String fileName, boolean isLogin) {
        this.url = url;
        this.status = status;
        this.fileName = fileName;
        this.isLogin = isLogin;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getFileName() {
        return fileName;
    }

    public static Optional<ResponsePage> fromUrl(String url) {
        Optional<Session> session = SessionManager.getInstance().findSession(HttpCookie.getId());

        for (ResponsePage page : values()) {
            if (page.url.equals(url) && page.isLogin == session.isPresent()) {
                return Optional.of(page);
            }
        }
        return Optional.empty();
    }
}
