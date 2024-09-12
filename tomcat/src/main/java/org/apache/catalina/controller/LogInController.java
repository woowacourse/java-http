package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.exception.ApplicationException;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogInController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LogInController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) throws ApplicationException {
        String account = request.body().getAttribute("account");
        String password = request.body().getAttribute("password");
        Optional<User> optionalUser = InMemoryUserRepository.findByAccountAndPassword(account, password);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            log.info("optionalUser : {}", user);
            String sessionId = SessionManager.add(new Session(user));

            response.setStatusCode(HttpStatusCode.REDIRECT);
            response.setCookie("JSESSIONID", sessionId);
            response.setLocation("/index.html");
        }

        response.setStatusCode(HttpStatusCode.REDIRECT);
        response.setLocation("/401.html");
    }
}
