package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UserException;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import org.apache.coyote.http11.FileReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpResponseHeaders;
import org.apache.coyote.http11.response.HttpStatusCode;

public class RegisterController {

    private static final RegisterController instance = new RegisterController();

    private RegisterController() {
    }

    public HttpResponse register(HttpRequest httpRequest) throws URISyntaxException, IOException {
        FileReader fileReader = FileReader.getInstance();
        HttpStatusCode statusCode = HttpStatusCode.OK;
        httpRequest.setHttpRequestPath("/index.html");
        String account = httpRequest.getRequestBodyValue("account");
        String email = httpRequest.getRequestBodyValue("email");
        String password = httpRequest.getRequestBodyValue("password");
        try {
            User user = new User(account, password, email);
            checkDuplicatedUser(user);
            InMemoryUserRepository.save(user);
        } catch (UserException e) {
            httpRequest.setHttpRequestPath("/register.html");
            statusCode = HttpStatusCode.BAD_REQUEST;
        }
        HttpResponseBody httpResponseBody = new HttpResponseBody(
                fileReader.readFile(httpRequest.getHttpRequestPath()));

        HttpResponseHeaders httpResponseHeaders = new HttpResponseHeaders(new HashMap<>());
        httpResponseHeaders.setContentType(httpRequest);
        httpResponseHeaders.setContentLength(httpResponseBody);
        return new HttpResponse(statusCode, httpResponseHeaders, httpResponseBody);
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
