package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.http11.constant.HttpHeader;
import org.apache.coyote.http11.constant.HttpMethod;
import org.apache.coyote.http11.cookie.Cookie;

public class HttpRequest {

    private static final String PARAM_DELIMITER = "&";
    private static final String KEYVALUE_DELIMITER = "=";
    private static final String COOKIE_DELIMITER = ";";
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

        return url.endsWith(".html") || url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".ico");
    }

    public boolean hasCookie() {
        return header.containsKey(HttpHeader.COOKIE.value());
    }

    public Cookie getCookies() {
        String rawCookie = header.get(HttpHeader.COOKIE.value());
        Cookie cookie = new Cookie();

        if (rawCookie == null) {
            return cookie;
        }

        String[] eachCookies = rawCookie.split(COOKIE_DELIMITER);
        for (String eachCookie : eachCookies) {
            String[] params = eachCookie.split(KEYVALUE_DELIMITER);
            String cookieKey = params[0].strip();
            String cookieValue = params[1].strip();
            cookie.setCookie(cookieKey, cookieValue);
        }

        return cookie;
    }

    public Map<String, String> getBody() {
        Map<String, String> bodies = new HashMap<>();
        String[] rawBodies = body.split(PARAM_DELIMITER);

        for (String rawBody : rawBodies) {
            String[] params = rawBody.split(KEYVALUE_DELIMITER);
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
