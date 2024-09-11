package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final String REGISTER_PATH = "/register";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String INDEX_PATH = "/index.html";

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        String account = httpRequest.getBodyValue(ACCOUNT);
        String password = httpRequest.getBodyValue(PASSWORD);
        String email = httpRequest.getBodyValue(EMAIL);

        if (InMemoryUserRepository.containsByAccount(account)) {
            log.error("이미 존재하는 account입니다");
            return HttpResponse.found(httpRequest)
                    .location(REGISTER_PATH)
                    .build();
        }

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        return HttpResponse.found(httpRequest)
                .location(INDEX_PATH)
                .build();
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        try {
            return HttpResponse.ok(httpRequest)
                    .staticResource(httpRequest.getPath())
                    .build();
        } catch (URISyntaxException | IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
