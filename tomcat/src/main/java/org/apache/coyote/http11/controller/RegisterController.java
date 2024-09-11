package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final String REGISTER_PATH = "/register";

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        String account = httpRequest.getBodyValue("account");
        String password = httpRequest.getBodyValue("password");
        String email = httpRequest.getBodyValue("email");

        if (InMemoryUserRepository.containsByAccount(account)) {
            log.error("이미 존재하는 account입니다");
            return HttpResponse.found(httpRequest)
                    .location(REGISTER_PATH)
                    .build();
        }

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        return HttpResponse.found(httpRequest)
                .location("/index.html")
                .build();
    }

    private boolean checkToken(String[] token) {
        return Arrays.stream(token).anyMatch(t -> t.split("=").length < 2);
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
