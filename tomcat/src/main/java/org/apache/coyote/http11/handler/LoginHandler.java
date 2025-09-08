package org.apache.coyote.http11.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.coyote.http11.dto.HttpRequest;
import org.apache.coyote.http11.helper.Responses;
import org.apache.coyote.http11.util.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.path().startsWith("/login");
    }

    @Override
    public void handle(HttpRequest request, OutputStream outputStream) throws IOException {
        System.out.println(request.method());
        if ("GET".equalsIgnoreCase(request.method())) {
            serveStaticResponse(request, outputStream, HttpStatus.OK, "login.html");
            return;
        }

        if (!"POST".equalsIgnoreCase(request.method())) {
            Responses.text(outputStream, request.version(), HttpStatus.METHOD_NOT_ALLOWED.getCode(), HttpStatus.METHOD_NOT_ALLOWED.getReason(), "Method Not Allowed");
            return;
        }

        String account = request.getParam("account");
        String password = request.getParam("password");

        if (account == null || password == null) {
            serveStaticResponse(request, outputStream, HttpStatus.NOT_FOUND, "401.html");
            return;
        }
        try {
            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + account));
            if (user.checkPassword(password)) {
                serveStaticResponse(request, outputStream, HttpStatus.FOUND, "index.html");
                return;
            }
            serveStaticResponse(request, outputStream, HttpStatus.NOT_FOUND, "401.html");
        } catch (IllegalArgumentException e) {
            log.warn("로그인 실패 - {}", e.getMessage());
            serveStaticResponse(request, outputStream, HttpStatus.NOT_FOUND, "401.html");
        }
    }

    private void serveStaticResponse(HttpRequest request, OutputStream outputStream, HttpStatus httpStatus, String filePath) throws IOException {
        String basePath = "static/";
        try (var inputStream = getClass().getClassLoader().getResourceAsStream(basePath + filePath)) {
            if (inputStream == null) {
                Responses.notFound(outputStream, request.version());
                return;
            }
            byte[] bytes = inputStream.readAllBytes();
            Responses.binary(outputStream, request.version(), httpStatus.getCode(), httpStatus.getReason(), "text/html", bytes);
        }
    }
}
