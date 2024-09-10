package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import org.apache.catalina.util.ResourceReader;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        HttpMethod method = request.getMethod();

        if (method.isPost()) {
            doPost(request, response);
        }

        if (method.isGet()) {
            doGet(response);
        }
    }

    public void doGet(HttpResponse response) throws IOException {
        ResourceReader.serveResource("/register.html", response);
    }

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
