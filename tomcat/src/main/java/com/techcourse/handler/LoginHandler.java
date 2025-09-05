package com.techcourse.handler;

import static org.apache.coyote.HttpStatus.OK;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpRequestHandler;
import org.apache.coyote.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
    private static final String LOGIN_FILE_PATH = "static/login.html";

    @Override
    public void handleGet(HttpRequest request, HttpResponse response) {
        final Optional<String> contentOpt = getResourceContent(LOGIN_FILE_PATH);

        if (contentOpt.isEmpty()) {
            throw new NoSuchElementException("해당 경로에 파일이 존재하지 않습니다: " + request.getPath());
        }

        final String content = contentOpt.get();
        response.setStatus(OK);
        response.setContentType("text/html;charset=utf-8");
        response.setBody(content);

        logLoginSuccess(request);
    }

    private void logLoginSuccess(HttpRequest request) {
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");

        InMemoryUserRepository.findByAccount(account).ifPresent(
                user -> {
                    if (user.checkPassword(password)) {
                        log.info("user : {}", user);
                    }
                }
        );
    }

    private Optional<String> getResourceContent(String resourcePath) {
        try (final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {

            if (inputStream == null) {
                return Optional.empty();
            }

            return Optional.of(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }
}
