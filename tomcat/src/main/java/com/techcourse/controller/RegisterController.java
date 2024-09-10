package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.List;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.controller.Handler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Queries;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final String REGISTER_PATH = "/register";
    private static final String REGISTER_PAGE = "/register.html";

    public RegisterController() {
        List<Handler> handlers = List.of(
                Handler.ofPost(REGISTER_PATH, this::doRegisterPost),
                Handler.ofGet(REGISTER_PATH, this::doRegisterGet)
        );
        registerHandlers(handlers);
    }

    private void doRegisterGet(HttpRequest request, HttpResponse response) {
        ResponseFile registerFile = ResponseFile.of(REGISTER_PAGE);
        response.addFile(registerFile);
        response.setHttpStatus(HttpStatus.OK);
    }

    private void doRegisterPost(HttpRequest request, HttpResponse response) {
        Queries queries = Queries.of(request.getBody());
        String account = queries.get("account");
        User user = new User(account, queries.get("password"), queries.get("email"));

        validateRegisterAccount(account);
        InMemoryUserRepository.save(user);

        response.setHttpStatus(HttpStatus.FOUND);
        response.addRedirectHeader("/index.html");
    }

    private void validateRegisterAccount(String account) {
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            log.warn("이미 존재하는 사용자입니다: " + account);
            throw new IllegalArgumentException("중복된 계정을 생성할 수 없습니다.");
        }
    }
}
