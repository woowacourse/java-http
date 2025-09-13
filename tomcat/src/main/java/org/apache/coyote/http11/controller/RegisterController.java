package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.Http11Session;
import org.apache.coyote.http11.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected void doGet(Http11Request request, Http11Response response) throws Exception {
        response.putHeader("Content-Type", "text/html; charset=utf-8");
        response.readFileFromClasspath("static/register.html");
    }

    @Override
    protected void doPost(Http11Request request, Http11Response response) throws Exception {
        final User user = new User(request.getParam("account"), request.getParam("password"), request.getParam("email"));
        InMemoryUserRepository.save(user);
        log.info("User saved: {}", user);

        Http11Session session = SessionManager.createSession();
        session.setAttribute("user", user);
        response.putHeader("Set-Cookie", "JSESSIONID=" + session.getId() + "; Path=/");
        response.sendRedirect("/index.html");
    }
}
