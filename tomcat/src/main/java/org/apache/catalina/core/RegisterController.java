package org.apache.catalina.core;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.mapping.AbstractController;
import org.apache.coyote.Session;
import org.apache.coyote.util.StaticResourceHandler;
import org.apache.coyote.util.request.HttpRequest;
import org.apache.coyote.util.response.HttpResponse;
import org.apache.coyote.util.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        Session existingSession = request.getSession(false);
        if (existingSession != null && existingSession.getAttribute("user") != null) {
            response.sendRedirect("/index.html");
            return;
        }
        byte[] body = StaticResourceHandler.readResource("static/register.html");
        if (body == null) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.addHeader("Content-Type", "text/html;charset=utf-8");
            response.setBody("Register page not found".getBytes());
            return;
        }
        response.setStatus(HttpStatus.OK);
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.setBody(body);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getBody().get("account");
        String password = request.getBody().get("password");
        String email = request.getBody().get("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("User : {}", user);

        response.sendRedirect("/index.html");
    }
}
