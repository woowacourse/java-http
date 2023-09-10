package org.apache.servlet.customservlet;

import java.io.IOException;
import java.util.Objects;
import org.apache.coyote.http11.MemberService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.http11.response.ResponseUtil;
import org.apache.servlet.ServletException;
import org.apache.servlet.SimpleHttpServlet;
import org.apache.servlet.SimpleWebServlet;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

@SimpleWebServlet("/login")
public class LoginServlet extends SimpleHttpServlet {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws ServletException, IOException {
        if (request.containsCookie()) {
            String cookies = request.getHeader("Cookie");
            for (String cookie : cookies.split(";")) {
                String[] probableSessionCookie = cookie.split("=");
                if (probableSessionCookie.length == 2) {
                    String sessionId = probableSessionCookie[1];
                    Session session = SessionManager.findSession(sessionId);
                    if (Objects.nonNull(session)) {
                        response.setHttpStatusCode(HttpStatusCode.FOUND);
                        response.setLocation("/index.html");
                        response.setCookie("JSESSIONID=" + session.getId());
                        return;
                    }
                }
            }
        }
        ResponseUtil.buildStaticFileResponse(response, "/login.html");
    }

    @Override
    public void doPost(
            HttpRequest request, HttpResponse response) throws ServletException, IOException {
        validateContainsFormData(request);
        MemberService.login(request, response);
    }

    private void validateContainsFormData(HttpRequest req) {
        if (!req.containsFormData()) {
            throw new ServletException();
        }
    }
}
