package com.techcourse.servlet;

import com.techcourse.db.InMemoryUserRepository;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
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
        try {
            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            if (resource == null) {
                return "<html><body><h1>Login page not found</h1></body></html>";
            }
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (final Exception e) {
            log.error("Failed to read login.html", e);
            return "<html><body><h1>Error loading login page</h1></body></html>";
        }
    }

    @Override
    public void destroy() {
        log.info("LoginServlet destroyed");
    }
}
