package org.apache.coyote.http11.request;

import org.apache.coyote.http11.response.HttpStatus;

import static org.apache.coyote.http11.response.HttpStatus.CONFLICT;
import static org.apache.coyote.http11.response.HttpStatus.FOUND;
import static org.apache.coyote.http11.response.HttpStatus.OK;
import static org.apache.coyote.http11.response.HttpStatus.UNAUTHORIZED;

public enum Page {

    INDEX_PAGE("/index.html", FOUND),
    REGISTER_PAGE("/register.html", OK),
    LOGIN_PAGE("/login.html", OK),
    CONFLICT_PAGE("/409.html", CONFLICT),
    UNAUTHORIZED_PAGE("/401.html", UNAUTHORIZED);

    private final String redirectUrl;
    private final HttpStatus statusCode;

    Page(String redirectUrl, HttpStatus statusCode) {
        this.redirectUrl = redirectUrl;
        this.statusCode = statusCode;
    }

    public String redirectUrl() {
        return redirectUrl;
    }

    public HttpStatus statusCode() {
        return statusCode;
    }

}
