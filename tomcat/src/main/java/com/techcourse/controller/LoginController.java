package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.session.Session;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getParameters("account");
        String password = request.getParameters("password");

        Session session = request.getSession();

        Optional<User> authenticatedUser = authenticate(account, password);
        if (authenticatedUser.isPresent()) {
            User user = authenticatedUser.get();
            session.setAttribute("user", user);

            log.info("로그인 성공! 아이디 : {}", user.getAccount());
            response.sendRedirect("/index.html");
        } else {
            response.sendRedirect("/401.html");
        }
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Session session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user != null) {
            response.sendRedirect("/index.html");
        } else {
            String body = resourceReader.read("/login.html");
            response.setBody(body);
        }
    }

    private Optional<User> authenticate(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }
}
