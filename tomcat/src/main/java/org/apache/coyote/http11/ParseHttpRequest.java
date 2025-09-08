package org.apache.coyote.http11;

import java.util.Map;

public record ParseHttpRequest(
        String method,
        String httpRequest,
        Map<String, String> requestBody,
        HttpCookies cookies,
        Session session
) {

    public ParseHttpRequest addRequestBody(Map<String, String> requestBody) {
        return new ParseHttpRequest(method, httpRequest, requestBody, cookies, session);
    }

    public ParseHttpRequest addCookies(Map<String, String> cookies) {
        return new ParseHttpRequest(method, httpRequest, requestBody, new HttpCookies(cookies), session);
    }

    public ParseHttpRequest addSession(Session session) {
        return new ParseHttpRequest(method, httpRequest, requestBody, cookies, session);
    }
}
