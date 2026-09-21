package com.techcourse.model;

import java.util.UUID;

public class HttpCookie {
    private final String JSESSIONID;

    private HttpCookie() {
        this.JSESSIONID = UUID.randomUUID().toString();
    }

    public static HttpCookie makeJsessionid() {
        return new HttpCookie();
    }

    @Override
    public String toString() {
        return "JSESSIONID=" + JSESSIONID;
    }

}
