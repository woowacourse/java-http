package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.Http11Cookie;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.Http11Session;
import org.apache.coyote.http11.MediaType;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.exception.Http11ParseException;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(Http11Request request, Http11Response response) throws Http11ParseException {
        final Http11Cookie cookie = request.getCookie();
        if (cookie.isContainsSessionId() && SessionManager.getInstance().containsSession(cookie.getSessionId())) {
            response.sendRedirect("/index.html");
            return;
        }
        response.putHeader("Content-Type", MediaType.HTML.getMimeType());
        response.readFileFromClasspath("static/login.html");
    }

    @Override
    protected void doPost(Http11Request request, Http11Response response) throws Http11ParseException {
        try {
            final User user = InMemoryUserRepository.findByAccount(request.getParam("account"))
                    .orElseThrow(() -> new IllegalArgumentException("[ERROR] 회원을 찾을 수 없습니다."));

            if (user.checkPassword(request.getParam("password"))) {
                final Http11Session session = SessionManager.createSession();
                session.setAttribute("user", user);
                response.putHeader("Set-Cookie", "JSESSIONID=" + session.getId() + "; Path=/");
                response.sendRedirect("/index.html");
                return;
            }
            response.sendRedirect("/401.html");
        } catch (IllegalArgumentException e) {
            response.sendRedirect("/401.html");
        }
    }
}
