package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        if (request.getSession().getAttribute("user") != null) {
            response.sendRedirect("/index.html");
            return;
        }
        if (request.getParameters().isEmpty()) {
            response.setBody(readResource("/login.html"), "text/html");
            return;
        }
        login(request, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        login(request, response);
    }

    private void login(final HttpRequest request, final HttpResponse response) {
        final Optional<User> user = authenticate(request.getParameter("account"), request.getParameter("password"));
        user.ifPresent(foundUser -> request.getSession().setAttribute("user", foundUser));
        response.sendRedirect(user.isPresent() ? "/index.html" : "/401.html");
    }

    private Optional<User> authenticate(final String account, final String password) {
        if (account == null || password == null) {
            return Optional.empty();
        }
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> {
                    log.info("로그인 사용자 조회 성공: {}", user);
                    return user;
                });
    }

    private byte[] readResource(final String uri) throws IOException {
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream("static" + uri)) {
            if (resource == null) {
                throw new IOException("정적 리소스를 찾을 수 없습니다: " + uri);
            }
            return resource.readAllBytes();
        }
    }
}
