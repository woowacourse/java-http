package org.apache.catalina.core;

import static org.apache.catalina.core.session.SessionManager.SESSION_ID_COOKIE_NAME;
import static org.apache.coyote.http11.common.Protocol.HTTP11;
import static org.apache.coyote.http11.common.header.HeaderName.ACCEPT;
import static org.apache.coyote.http11.common.header.HeaderName.COOKIE;

import java.util.Map;
import org.apache.catalina.core.servlet.HttpServletRequest;
import org.apache.catalina.core.session.SessionManager;
import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.http11.request.Request;

public class HttpServletRequestFixture {

    private static final SessionManager sessionManger = new SessionManager();

    public static HttpServletRequest createGet(final String uri, final String acceptValue) {
        return new HttpServletRequest(
                Request.of("get", uri, HTTP11.getValue(), Map.of(ACCEPT.getValue(), acceptValue), ""),
                sessionManger
        );
    }

    public static HttpServletRequest createPost(final String uri, final String sessionId, final String body) {
        return new HttpServletRequest(
                Request.of("post", uri, HTTP11.getValue(),
                        Map.of(COOKIE.getValue(), Cookies.ofSingleHeader(SESSION_ID_COOKIE_NAME, sessionId)),
                        body),
                sessionManger
        );
    }
}
