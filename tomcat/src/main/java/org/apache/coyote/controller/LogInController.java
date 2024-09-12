package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogInController extends Controller {

    private static final Logger log = LoggerFactory.getLogger(LogInController.class);

    @Override
    public HttpResponse process(HttpRequest request) {
        String account = request.body().getAttribute("account");
        String password = request.body().getAttribute("password");
        Optional<User> optionalUser = InMemoryUserRepository.findByAccountAndPassword(account, password);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            log.info("optionalUser : {}", user);
            String sessionId = SessionManager.add(new Session(user));
            return new HttpResponse(
                    HttpStatusCode.REDIRECT,
                    Map.of("Location", "/index.html",
                            "Set-Cookie", "JSESSIONID=" + sessionId),
                    null
            );
        }

        return new HttpResponse(
                HttpStatusCode.REDIRECT,
                Map.of("Location", "/401.html"),
                null
        );
    }
}
