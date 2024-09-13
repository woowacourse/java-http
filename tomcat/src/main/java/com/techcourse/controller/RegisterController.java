package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UserException;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class RegisterController extends AbstractController {

    private static final RegisterController instance = new RegisterController();

    private static final String REGISTER_ACCOUNT_KEY = "account";

    private static final String REGISTER_EMAIL_KEY = "email";

    private static final String REGISTER_PASSWORD_KEY = "password";

    private static final String INDEX_PAGE = "/index.html";

    private static final String REGISTER_PAGE = "/register.html";

    private RegisterController() {
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws URISyntaxException, IOException {
        String account = request.getRequestBodyValue(REGISTER_ACCOUNT_KEY);
        String email = request.getRequestBodyValue(REGISTER_EMAIL_KEY);
        String password = request.getRequestBodyValue(REGISTER_PASSWORD_KEY);
        try {
            User user = new User(account, password, email);
            checkDuplicatedUser(user);
            InMemoryUserRepository.save(user);
            redirect(response, INDEX_PAGE);
        } catch (UserException e) {
            setFailResponse(request, response);
        }
        setResponseContent(request, response);
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws URISyntaxException, IOException {
        response.setHttpStatusCode(HttpStatusCode.OK);
        setResponseContent(request, response);
    }

    private void checkDuplicatedUser(User user) {
        String userAccount = user.getAccount();
        InMemoryUserRepository.findByAccount(userAccount)
                .ifPresent(foundUser -> {
                    throw new UserException(userAccount + "는 이미 존재하는 계정입니다.");
                });
    }

    private void setFailResponse(HttpRequest request, HttpResponse response) {
        request.setHttpRequestPath(REGISTER_PAGE);
        response.setHttpStatusCode(HttpStatusCode.BAD_REQUEST);
    }

    public static RegisterController getInstance() {
        return instance;
    }
}
