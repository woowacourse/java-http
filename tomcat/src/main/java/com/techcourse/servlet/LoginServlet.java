package com.techcourse.servlet;

import com.techcourse.db.InMemoryUserRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.catalina.Servlet;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServlet implements Servlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    @Override
    public void init() {
        log.info("LoginServlet initialized");
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        if ("GET".equals(request.getMethod())) {
            handleGet(request, response);
            return;
        }

        response.setStatus(405);
        response.write("<html><body><h1>405 Method Not Allowed</h1></body></html>");
    }

    private void handleGet(final HttpRequest request, final HttpResponse response) {
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");

        if (account != null && password != null) {
            processLogin(account, password);
        }

        final String loginHtml = readLoginPage();
        response.write(loginHtml);
    }

    private void processLogin(final String account, final String password) {
        final var userOptional = InMemoryUserRepository.findByAccount(account);

        if (userOptional.isEmpty()) {
            log.info("로그인 실패: 존재하지 않는 계정 - account: {}", account);
            return;
        }

        final var user = userOptional.get();
        if (user.checkPassword(password)) {
            log.info("로그인 성공: 회원 조회 결과 - {}", user);
            return;
        }

        log.info("로그인 실패: 비밀번호 불일치 - account: {}", account);
    }

    private String readLoginPage() {
        try (final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/login.html");
             final BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8))) {

            return reader.lines()
                    .collect(Collectors.joining(System.lineSeparator()));

        } catch (final IOException e) {
            log.error("Failed to read login.html", e);
            return createErrorPage("Error loading login page", 500);
        }
    }

    private String createErrorPage(final String message, final int statusCode) {
        return String.format("""
                <html>
                <head><title>Error %d</title></head>
                <body>
                    <h1>%s</h1>
                    <p>Status Code: %d</p>
                </body>
                </html>
                """, statusCode, message, statusCode);
    }

    @Override
    public void destroy() {
        log.info("LoginServlet destroyed");
    }
}
