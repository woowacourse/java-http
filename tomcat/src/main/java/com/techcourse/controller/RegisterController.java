package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.Http11Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected Http11Response doPost(Http11Request http11Request) throws IOException {
        Map<String, String> parseQuery = http11Request.parseBody();

        String account = parseQuery.get("account");
        String email = parseQuery.get("email");
        String password = parseQuery.get("password");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        String path = "index.html";
        String contentType = guessContentTypeByFileExtension(path);
        byte[] body = readFromResourcePath(path);

        return new Http11Response(body,contentType, Http11Status.FOUND);
    }
}
