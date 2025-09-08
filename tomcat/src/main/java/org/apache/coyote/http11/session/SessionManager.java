package org.apache.coyote.http11.session;

import java.util.UUID;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;

public class SessionManager {

    private static final String RESPONSE_HEADER_COOKIE_NAME = "Set-Cookie";
    private static final String SESSION_COOKIE_KEY = "JSESSIONID";

    public void setSessionCookie(final HttpRequest request, final HttpResponse response) {
        final HttpCookie cookie = request.getCookie();
        String sessionId = cookie.get(SESSION_COOKIE_KEY);
        if (sessionId == null) {
            response.addHeader(RESPONSE_HEADER_COOKIE_NAME,
                    String.format("%s=%s", SESSION_COOKIE_KEY, UUID.randomUUID()));
        }
    }
}
