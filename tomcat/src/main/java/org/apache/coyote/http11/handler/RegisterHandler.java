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

public class RegisterHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(RegisterHandler.class);
    private Long userId = 2L;

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.path().startsWith("/register");
    }

    @Override
    public void handle(HttpRequest request, OutputStream outputStream) throws IOException {
        if ("GET".equalsIgnoreCase(request.method())) {
            serveStaticResponse(request, outputStream, HttpStatus.OK, "register.html");
            return;
        }

        if (!"POST".equalsIgnoreCase(request.method())) {
            Responses.text(outputStream, request.version(), HttpStatus.METHOD_NOT_ALLOWED.getCode(), HttpStatus.METHOD_NOT_ALLOWED.getReason(), "Method Not Allowed");
            return;
        }

        String account = request.getParam("account");
        String email = request.getParam("email");
        String password = request.getParam("password");

        if (account == null || password == null || email == null) {
            serveStaticResponse(request, outputStream, HttpStatus.NOT_FOUND, "401.html");
            return;
        }
        try {
            User user = new User(userId++, account, password, email);
            if (InMemoryUserRepository.has(user)) {
                log.error("이미 존재하는 아이디입니다." + user.getAccount());
                throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
            }
            InMemoryUserRepository.save(user);
            log.info("회원가입 완료 = {}, {}, {}", account, email, password);
            serveStaticResponse(request, outputStream, HttpStatus.CREATED, "index.html");
        } catch (IllegalArgumentException e) {
            log.warn("회원가입실패 실패 - {}", e.getMessage());
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
