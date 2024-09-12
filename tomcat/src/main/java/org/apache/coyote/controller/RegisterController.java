package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends Controller {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    public HttpResponse process(HttpRequest request) {
        String account = request.body().getAttribute("account");
        String email = request.body().getAttribute("email");
        String password = request.body().getAttribute("password");

        User user = InMemoryUserRepository.save(new User(account, password, email));
        log.info("Register success: { account: {}, email: {}, password:{}}", account, email, password);
        String sessionId = SessionManager.add(new Session(user));

        return new HttpResponse(
                HttpStatusCode.REDIRECT,
                Map.of("Location", "/index.html",
                        "Set-Cookie", "JSESSIONID=" + sessionId),
                null
        );
    }
}
