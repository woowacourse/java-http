package org.apache.coyote.http11.handler;

import java.util.Objects;
import org.apache.coyote.http11.MemberService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.http11.response.ResponseUtil;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class LoginHandler implements Handler {

    @Override
    public boolean supports(HttpRequest request) {
        return request.getUrl().equals("/login");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        if (doesRequestContainsCookie(request)) {
            String cookies = request.getHeader("Cookie");
            for (String cookie : cookies.split(";")) {
                String[] probableSessionCookie = cookie.split("=");
                if (probableSessionCookie.length == 2) {
                    String sessionId = probableSessionCookie[1];
                    Session session = SessionManager.findSession(sessionId);
                    if (Objects.nonNull(session)) {
                        return new HttpResponse.Builder()
                                .setHttpStatusCode(HttpStatusCode.FOUND)
                                .setLocation("/index.html")
                                .setCookie("JSESSIONID=" + session.getId()).build();
                    }
                }
            }
        }
        if (doesRequestContainsFormData(request)) {
            return MemberService.login(request);
        }
        return returnLoginPage();
    }

    private boolean doesRequestContainsCookie(HttpRequest request) {
        return request.containsHeader("Cookie");
    }

    private boolean doesRequestContainsFormData(HttpRequest request) {
        return request.containsHeader("Content-Type") && request.getHeader("Content-Type")
                .contains("application/x-www-form-urlencoded");
    }

    private HttpResponse returnLoginPage() {
        String url = "/login.html";
        return ResponseUtil.buildStaticFileResponse(url);
    }
}
