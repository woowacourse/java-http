package org.apache.catalina.core;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.mapping.AbstractController;
import org.apache.coyote.util.request.HttpRequest;
import org.apache.coyote.util.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

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
