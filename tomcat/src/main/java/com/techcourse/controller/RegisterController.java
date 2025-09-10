package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.util.ResourceParser;
import java.util.Optional;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpResponse;
import org.apache.coyote.http11.httpResponse.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) throws Exception {
        return HttpResponse.status(HttpStatus.OK)
                .build(Page.REGISTER.getPath(), ResourceParser.parse(Page.REGISTER.getPath()));
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) throws Exception {
        try {
            final String account = findValueFromParams(httpRequest, "account");
            final String password = findValueFromParams(httpRequest, "password");
            final String email = findValueFromParams(httpRequest, "email");

            final Optional<User> userOrEmpty = InMemoryUserRepository.findByAccount(account);
            if (userOrEmpty.isPresent()) {
                log.warn("id: {}", account);
                throw new IllegalArgumentException("이미 가입된 계정입니다: " + account);
            }

            final User user = new User(account, password, email);
            InMemoryUserRepository.save(user);

            return HttpResponse.status(HttpStatus.FOUND)
                    .location(Page.INDEX.getPath());
        } catch (IllegalArgumentException e) {
            return HttpResponse.status(HttpStatus.BAD_REQUEST)
                    .build(Page.BAD_REQUEST.getPath(), ResourceParser.parse(Page.BAD_REQUEST.getPath()));
        }
    }

    private String findValueFromParams(
            final HttpRequest httpRequest,
            final String name
    ) {
        return httpRequest.findParamsValueFromBody(name)
                .orElseThrow(() -> new IllegalArgumentException("파라미터의 키 값이 존재하지 않습니다: " + name));
    }
}
