package org.apache.coyote.http11.request;

import java.util.UUID;

public class HttpCookie {
    private static final String SESSION_ID = "JSESSIONID";

    private String values;

    public HttpCookie(){
    }
    private HttpCookie(String values) {
        this.values = values;
    }

    public static HttpCookie create(HttpHeaders httpHeaders) {
        String cookie = httpHeaders.get("Cookie");
        if (cookie == null) {
            UUID uuid = UUID.randomUUID();
            cookie = SESSION_ID + "=" + uuid;
            return new HttpCookie(cookie);
        }
        if (cookie.contains(SESSION_ID)) {
            return new HttpCookie(cookie);
        }
        UUID uuid = UUID.randomUUID();
        cookie = cookie + SESSION_ID + "=" + uuid;
        return new HttpCookie(cookie);
    }

    public boolean isEmpty() {
        return values == null;
    }

    public String getResponse(){
        return "Set-Cookie: " + values;
    }

    public String getValues() {
        return values;
    }
}
