package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;
import org.apache.coyote.http11.data.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginAbstractController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginAbstractController.class);

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        if (isLogin(request.getSession())) {
            response.setViewName("redirect:/index.html");
            return;
        }
        response.setViewName("/login.html");
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        final Session session = request.getSession();

        final Map<String, String> body = request.getBody();
        final String account = body.get("account");
        final String password = body.get("password");

        if (!isValidateData(account, password)) {
            response.badRequest();
            return;
        }

        final Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));

        if (optionalUser.isPresent()) {
            session.setAttribute("user", optionalUser.get());
            response.setViewName("redirect:/index.html");
            return;
        }

        response.setViewName("redirect:/401.html");
    }

    private boolean isLogin(Session session) {
        return session.getAttribute("user").isPresent();
    }

    private boolean isValidateData(String account, String password) {
        return !Objects.isNull(account) && !Objects.isNull(password);
    }

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.getRequestLine().getPath().equals("/login");
    }
}
