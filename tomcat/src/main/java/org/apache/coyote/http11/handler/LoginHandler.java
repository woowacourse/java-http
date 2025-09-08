package org.apache.coyote.http11.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.coyote.http11.dto.HttpRequest;
import org.apache.coyote.http11.helper.Responses;
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
        String account = request.getParam("account");
        String password = request.getParam("password");

        if (account != null && password != null) {
            try {
                User user = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + account));
                log.info("로그인 시도: account={}, 결과={}", account, user);
            } catch (IllegalArgumentException e) {
                log.warn("로그인 실패 - {}", e.getMessage());
                Responses.text(outputStream, request.version(), 401, "Unauthorized", "로그인 실패");
                return;
            }
        }

        try (var is = getClass().getClassLoader().getResourceAsStream("static/login.html")) {
            if (is == null) {
                Responses.notFound(outputStream, request.version());
                return;
            }
            byte[] bytes = is.readAllBytes();
            Responses.binary(outputStream, request.version(), 200, "OK", "text/html", bytes);
        }
    }
}
