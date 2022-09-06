package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.constant.HttpMethod;

public class HttpRequest {

    private final HttpStartLine httpStartLine;
    private final Map<String, String> header;
    private final String body;

    public HttpRequest(HttpStartLine httpStartLine, Map<String, String> header, String body) {
        this.httpStartLine = httpStartLine;
        this.header = header;
        this.body = body;
    }

    public boolean isResource() {
        String url = getUrl();

        return url.endsWith(".html") || url.endsWith(".css") || url.endsWith(".js");
    }

    public boolean hasCookie() {
        return header.containsKey("Cookie");
    }

    public Cookie getCookies() {
        String rawCookie = header.get("Cookie");
        Cookie cookie = new Cookie();

        if (rawCookie == null) {
            return cookie;
        }

        String[] eachCookies = rawCookie.split(";");
        for (String eachCookie : eachCookies) {
            String[] params = eachCookie.split("=");
            String cookieKey = params[0].strip();
            String cookieValue = params[1].strip();
            cookie.setCookie(cookieKey, cookieValue);
        }

        return cookie;
    }

    public Map<String, String> getBody() {
        Map<String, String> bodies = new HashMap<>();
        String[] rawBodies = body.split("&");

        for (String rawBody : rawBodies) {
            String[] params = rawBody.split("=");
            if (params.length != 2) {
                continue;
            }
            String paramName = params[0];
            String paramValue = params[1];

            bodies.put(paramName, paramValue);
        }

        return bodies;
    }

    public String getUrl() {
        return httpStartLine.getUrl();
    }

    public boolean compareUrl(String url) {
        return httpStartLine.getUrl()
                .equals(url);
    }

    public HttpMethod getMethod() {
        return httpStartLine.getMethod();
    }

    public Map<String, String> getQueryString() {
        return httpStartLine.getQueryString();
    }
}
