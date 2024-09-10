package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import org.apache.catalina.util.ResourceReader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        ResourceReader.serveResource("/register.html", response);
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getBody("account");
        String password = request.getBody("password");
        String email = request.getBody("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        log.info("회원 가입 성공 :: account = {}", user.getAccount());
        response.setRedirect("/index.html");
    }
}
