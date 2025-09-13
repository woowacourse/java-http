package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        renderPage(request, response, "/register.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        if (request.equalContentType("application/x-www-form-urlencoded")) {
            register(request, response);
        }
    }

    private void register(
            final HttpRequest request,
            final HttpResponse response
    ) {
        final Map<String, String> formParams = request.getFormParams();
        final String account = formParams.get("account");
        final String email = formParams.get("email");
        final String password = formParams.get("password");
        final User user = new User(account, password, email);

        if (account != null && email != null && password != null) {
            InMemoryUserRepository.save(user);
            log.info("회원가입 완료 {}", user);
            redirect(request, response, "/index.html");
        }
    }
}
