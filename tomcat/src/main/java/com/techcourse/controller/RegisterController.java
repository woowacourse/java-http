package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.Request;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String USER = "user";

    private static final String REGISTER_PAGE = "/register.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

    @Override
    protected HttpResponse doGet(final Request request) throws Exception {
        return HttpResponses.render(REGISTER_PAGE);
    }

    @Override
    protected HttpResponse doPost(final Request request) {
        final String account = request.body().get(ACCOUNT);
        final String email = request.body().get(EMAIL);
        final String password = request.body().get(PASSWORD);

        if (account == null || email == null || password == null) {
            log.info("잘못된 회원가입 요청입니다.");

            return HttpResponses.redirect(UNAUTHORIZED_PAGE);
        }

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            log.info("이미 가입된 계정입니다: {}", account);

            return HttpResponses.redirect(UNAUTHORIZED_PAGE);
        }

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        final Session session = request.createSession();
        session.setAttribute(USER, user);

        return HttpResponses.redirect(INDEX_PAGE);
    }
}
