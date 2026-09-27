package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Set;
import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        response.sendStaticResource("/register.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final String account = request.getBodyParam("account");
        final String email = request.getBodyParam("email");
        final String password = request.getBodyParam("password");

        if (isBlank(account) || isBlank(email) || isBlank(password)) {
            log.info("회원가입에 필요한 정보가 입력되지 않았습니다.");
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            log.info("이미 존재하는 계정입니다. account: {}", account);
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }

        InMemoryUserRepository.save(new User(account, password, email));
        log.info("회원가입이 완료되었습니다. account: {}", account);
        response.sendRedirect(INDEX_PAGE);
    }

    private boolean isBlank(final String value) {
        return value == null || value.isBlank();
    }

    @Override
    protected Set<HttpMethod> allowedMethods() {
        return Set.of(HttpMethod.GET, HttpMethod.POST);
    }
}
