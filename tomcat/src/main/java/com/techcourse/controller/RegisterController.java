package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UserException;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private static final RegisterController instance = new RegisterController();

    private static final String REGISTER_ACCOUNT_KEY = "account";

    private static final String REGISTER_EMAIL_KEY = "email";

    private static final String REGISTER_PASSWORD_KEY = "password";

    private RegisterController() {
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws Exception {
        try {
            String account = request.getRequestBodyValue(REGISTER_ACCOUNT_KEY);
            String email = request.getRequestBodyValue(REGISTER_EMAIL_KEY);
            String password = request.getRequestBodyValue(REGISTER_PASSWORD_KEY);
            validateRegisterInput(account, email, password);
            User user = new User(account, password, email);
            checkDuplicatedUser(user);
            InMemoryUserRepository.save(user);
            redirect(response, PageEndpoint.INDEX_PAGE.getEndpoint());
        } catch (UserException | IllegalArgumentException e) {
            log.info(e.getMessage());
            setFailResponse(request, response);
        }
        setResponseContent(request, response);
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws URISyntaxException, IOException {
        response.setHttpStatusCode(HttpStatusCode.OK);
        setResponseContent(request, response);
    }

    private void validateRegisterInput(String account, String email, String password) {
        if (account.isEmpty()) {
            throw new IllegalArgumentException("아이디가 입력되지 않았습니다.");
        }
        if (email.isEmpty()) {
            throw new IllegalArgumentException("이메일이 입력되지 않았습니다.");
        }
        if (password.isEmpty()) {
            throw new IllegalArgumentException("비밀번호가 입력되지 않았습니다.");
        }
    }

    private void checkDuplicatedUser(User user) {
        String userAccount = user.getAccount();
        InMemoryUserRepository.findByAccount(userAccount)
                .ifPresent(foundUser -> {
                    throw new UserException(userAccount + "는 이미 존재하는 계정입니다.");
                });
    }

    private void setFailResponse(HttpRequest request, HttpResponse response) {
        request.setHttpRequestPath(PageEndpoint.REGISTER_PAGE.getEndpoint());
        response.setHttpStatusCode(HttpStatusCode.BAD_REQUEST);
    }

    public static RegisterController getInstance() {
        return instance;
    }
}
