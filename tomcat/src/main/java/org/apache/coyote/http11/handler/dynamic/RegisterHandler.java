package org.apache.coyote.http11.handler.dynamic;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.handler.statics.util.StaticResourceUtils;
import org.apache.coyote.http11.request.dto.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(RegisterHandler.class);
    private final AtomicLong sequence = new AtomicLong(2);

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.path().startsWith("/register");
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        switch (request.method().toUpperCase()) {
            case "POST" -> handlePost(request, response);
            default     -> StaticResourceUtils.serve(response, "404.html", HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    private void handlePost(HttpRequest request, HttpResponse response) throws IOException {
        String account = request.getParam("account");
        String email = request.getParam("email");
        String password = request.getParam("password");

        if (account == null || password == null || email == null) {
            StaticResourceUtils.serve(response, "401.html", HttpStatus.UNAUTHORIZED);
            return;
        }

        try {
            if (InMemoryUserRepository.has(account)) {
                log.error("이미 존재하는 아이디입니다: {}", account);
                throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
            }
            long id = sequence.getAndIncrement();
            User user = new User(id, account, password, email);
            InMemoryUserRepository.save(user);
            log.info("회원가입 완료 = {}, {}, {}", account, email, password);

            StaticResourceUtils.serve(response, "index.html", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.warn("회원가입 실패 - {}", e.getMessage());
            StaticResourceUtils.serve(response, "401.html", HttpStatus.UNAUTHORIZED);
        }
    }
}
