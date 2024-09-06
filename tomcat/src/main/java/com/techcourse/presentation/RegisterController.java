package com.techcourse.presentation;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import servlet.http.request.Request;
import servlet.http.response.Response;
import servlet.http.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private static RegisterController INSTANCE;

    private RegisterController() {
    }

    public static RegisterController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RegisterController();
        }
        return INSTANCE;
    }

    public void register(Request request, Response response) {
        String account = request.getBodyValue("account");
        String password = request.getBodyValue("password");
        String email = request.getBodyValue("email");
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("회원가입 성공! 아이디 : {}", user.getAccount());
        response.configureViewAndStatus("/index", StatusCode.FOUND);
    }
}
