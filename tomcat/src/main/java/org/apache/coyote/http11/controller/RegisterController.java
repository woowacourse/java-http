package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        registerUser(request);
        response.sendRedirect(Constants.DEFAULT_URI);
    }

    private void registerUser(HttpRequest request) {
        User user = generateUser(request);
        InMemoryUserRepository.save(user);

        log.info("회원가입 완료! 아이디: {}", user.getAccount());
    }

    private User generateUser(HttpRequest request) {
        String account = request.getBodyAttribute(Constants.PARAMETER_KEY_OF_ACCOUNT);
        String password = request.getBodyAttribute(Constants.PARAMETER_KEY_OF_PASSWORD);
        String email = request.getBodyAttribute(Constants.PARAMETER_KEY_OF_EMAIL);

        return new User(account, password, email);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
    }
}
