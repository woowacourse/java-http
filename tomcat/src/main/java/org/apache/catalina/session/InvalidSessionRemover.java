package org.apache.catalina.session;

import java.util.Map;
import org.apache.coyote.http11.data.HttpCookie;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;
import org.apache.coyote.http11.data.HttpStatusCode;
import org.apache.coyote.HttpVersion;

public class InvalidSessionRemover {
    public static HttpResponse remove(HttpRequest request) {
        String sessionId = request.getSessionId();
        if (sessionId != null && SessionManager.findSession(sessionId) == null) {
            HttpCookie cookie = new HttpCookie(HttpRequest.SESSION_ID_COOKIE_KEY, sessionId, Map.of("Max-Age", "0"));
            return new HttpResponse(HttpVersion.HTTP_1_1).addCookie(cookie)
                    .setHttpStatusCode(HttpStatusCode.FOUND)
                    .setRedirectUrl("/login.html");
        }
        return null;
    }
}
