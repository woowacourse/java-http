package com.techcourse.handler;

import static org.apache.coyote.HttpStatus.NOT_FOUND;
import static org.apache.coyote.HttpStatus.OK;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
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
        final URL resource = getClass().getClassLoader().getResource(LOGIN_FILE_PATH);
        if (resource == null) {
            response.setStatus(NOT_FOUND);
            response.setBody("파일이 존재하지 않습니다.");
            response.setContentType("text/plain;charset=utf-8");
            return;
        }
        final File loginFile = new File(resource.getPath());

        try {
            final String contentType = "text/html;charset=utf-8";
            final String responseBody = Files.readString(loginFile.toPath());

            response.setContentType(contentType);
            response.setBody(responseBody);
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }

        response.setStatus(OK);
        logLoginAttempt(request);
    }

    private void logLoginAttempt(HttpRequest request) {
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");

        InMemoryUserRepository.findByAccount(account).ifPresent(
            user -> {
                if(user.checkPassword(password)){
                    log.info("user : {}", user);
                }
            }
        );
    }
}
