package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UserException;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.http11.FileReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class RegisterController {

    private static final RegisterController instance = new RegisterController();

    private RegisterController() {
    }

    public void register(HttpRequest request, HttpResponse response) throws URISyntaxException, IOException {
        FileReader fileReader = FileReader.getInstance();
        request.setHttpRequestPath("/index.html");
        String account = request.getRequestBodyValue("account");
        String email = request.getRequestBodyValue("email");
        String password = request.getRequestBodyValue("password");
        try {
            User user = new User(account, password, email);
            checkDuplicatedUser(user);
            InMemoryUserRepository.save(user);
        } catch (UserException e) {
            request.setHttpRequestPath("/register.html");
            response.setHttpStatusCode(HttpStatusCode.BAD_REQUEST);
        }

        response.setHttpResponseBody(fileReader.readFile(request.getHttpRequestPath()));
        response.setHttpResponseHeader("Content-Type", request.getContentType() + ";charset=utf-8");
        response.setHttpResponseHeader("Content-Length", String.valueOf(response.getHttpResponseBody().body().getBytes().length));
    }

    private void checkDuplicatedUser(User user) {
        String userAccount = user.getAccount();
        InMemoryUserRepository.findByAccount(userAccount)
                .ifPresent(foundUser -> {
                    throw new UserException(userAccount + "는 이미 존재하는 계정입니다.");
                });
    }

    public static RegisterController getInstance() {
        return instance;
    }
}
