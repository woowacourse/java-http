package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterAbstractController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterAbstractController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        String account = request.body().getAttribute("account");
        String email = request.body().getAttribute("email");
        String password = request.body().getAttribute("password");

        User user = InMemoryUserRepository.save(new User(account, password, email));
        String sessionId = SessionManager.add(new Session(user));
        log.info("Register success: { account: {}, email: {}, password:{}}", account, email, password);

        response.setStatusCode(HttpStatusCode.REDIRECT);
        response.setCookie("JSESSIONID", sessionId);
    }
}
