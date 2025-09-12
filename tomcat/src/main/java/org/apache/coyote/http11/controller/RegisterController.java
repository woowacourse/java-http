package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.coyote.http11.handler.statics.util.StaticResourceUtils;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.http.request.dto.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private final AtomicLong sequence = new AtomicLong(2);

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getParam("account");
        String email = request.getParam("email");
        String password = request.getParam("password");

        if (account == null || password == null || email == null) {
            throw new IllegalArgumentException();
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

            response.sendRedirect("/index.html");
        } catch (IllegalArgumentException e) {
            log.warn("회원가입 실패 - {}", e.getMessage());
            throw e;
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        StaticResourceUtils.serve(response, "register.html", HttpStatus.OK);
    }
}
