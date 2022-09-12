package org.apache.coyote.http11;

import nextstep.jwp.model.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class CookieFilter {

    private static final String LOGIN = "login";
    private static final String LOGIN_HTML = "/login.html";
    private static final String SUCCEED_REDIRECT_URL = "/index.html";

    public static boolean doFilter(final HttpRequest request, final HttpResponse response) {
        try {
            if (request.getUrl().contains(LOGIN) && request.hasCookie()) {
                UserService.getInstance().validateUserBySession(request);
                response.found(SUCCEED_REDIRECT_URL);
                return true;
            }
            return false;
        } catch (RuntimeException e) {
            response.ok(LOGIN_HTML);
            return true;
        }
    }
}
