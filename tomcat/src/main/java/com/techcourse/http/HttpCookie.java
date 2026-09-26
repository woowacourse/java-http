package com.techcourse.http;


public class HttpCookie {
    private final String JSESSIONID;

    public HttpCookie(String jsessionid) {
        this.JSESSIONID = jsessionid;
    }

    @Override
    public String toString() {
        return "JSESSIONID=" + JSESSIONID;
    }

}
