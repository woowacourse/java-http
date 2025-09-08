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
import org.apache.catalina.Servlet;
import org.apache.catalina.Session;
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

        if ("POST".equals(request.getMethod())) {
            handlePost(request, response);
            return;
        }

        response.setStatus(405);
        response.write("<html><body><h1>405 Method Not Allowed</h1></body></html>");
    }

    private void handleGet(final HttpRequest request, final HttpResponse response) {
        // 이미 로그인된 상태인지 체크
        final Session session = request.getSession(false);
        if (session != null && getUser(session) != null) {
            // 이미 로그인됨 - "/"로 리다이렉트
            response.sendRedirect("/");
            return;
        }

        // 로그인 안 됨 - 로그인 페이지 보여줌
        final String loginHtml = readLoginPage();
        response.write(loginHtml);
    }

    private void handlePost(final HttpRequest request, final HttpResponse response) {
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");

        if (account == null || password == null) {
            response.sendRedirect("/login");
            return;
        }

        final var userOptional = InMemoryUserRepository.findByAccount(account);

        if (userOptional.isEmpty()) {
            log.info("로그인 실패: 존재하지 않는 계정 - account: {}", account);
            response.sendRedirect("/401.html");
            return;
        }

        final var user = userOptional.get();
        if (user.checkPassword(password)) {
            log.info("로그인 성공: 회원 조회 결과 - {}", user);

            // 세션에 사용자 정보 저장
            final Session session = request.getSession(true);
            session.setAttribute("user", user);
            response.addCookie("JSESSIONID", session.getId());
            response.sendRedirect("/");
            return;
        }

        log.info("로그인 실패: 비밀번호 불일치 - account: {}", account);
        response.sendRedirect("/401.html");
    }

    private User getUser(final Session session) {
        return (User) session.getAttribute("user");
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
