package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class RegisterController extends AbstractController{
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String REGISTER_PAGE = "/register.html";
    private static final String INDEX_PAGE = "/index.html";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        StaticResources.serve(REGISTER_PAGE, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        response.sendRedirect(register(request));
    }

    private String register(final HttpRequest request) {
        final Optional<String> account = request.getParameter(ACCOUNT);
        final Optional<String> password = request.getParameter(PASSWORD);
        final Optional<String> email = request.getParameter(EMAIL);
        if (account.isEmpty() || password.isEmpty() || email.isEmpty()) {
            log.info("회원 가입을 하기위해서는 셋 다 입력이 되어야 합니다.");
            return REGISTER_PAGE;
        }
        InMemoryUserRepository.save(new User(account.get(), password.get(), email.get()));
        return INDEX_PAGE;
    }
}
