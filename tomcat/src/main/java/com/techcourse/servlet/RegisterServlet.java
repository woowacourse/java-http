package com.techcourse.servlet;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.catalina.HttpServlet;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(RegisterServlet.class);

    @Override
    public void init() {
        log.info("RegisterServlet initialized");
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final String registerHtml = readRegisterPage();
        response.write(registerHtml);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final String account = request.getParameter("account");
        final String email = request.getParameter("email");
        final String password = request.getParameter("password");

        if (account == null || email == null || password == null) {
            log.warn("회원가입 실패: 필수 파라미터 누락, account={} email={} password={}", account, email, password);
            response.sendRedirect("/register");
            return;
        }

        if (processRegister(account, email, password)) {
            // 회원가입 성공 - "/"로 리다이렉트
            log.warn("회원가입 성공: account = {}, email = {}, password = {}", account, email, password);
            response.sendRedirect("/");
            return;
        }

        // 회원가입 실패 - 다시 회원가입 페이지로
        response.sendRedirect("/register");
    }

    private boolean processRegister(final String account, final String email, final String password) {
        try {
            final User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            log.info("회원가입 성공: {}", user);
            return true;
        } catch (final Exception e) {
            log.warn("회원가입 실패: account={}, email={}, error={}", account, email, e.getMessage());
            return false;
        }
    }

    private String readRegisterPage() {
        try (final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/register.html");
             final BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8))) {

            return reader.lines()
                    .collect(Collectors.joining(System.lineSeparator()));

        } catch (final IOException e) {
            log.error("Failed to read register.html", e);
            return createErrorPage("Error loading register page");
        }
    }


    @Override
    public void destroy() {
        log.info("RegisterServlet destroyed");
    }
}
