package org.apache.coyote.http11.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.enums.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LoginRequestHandler implements RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        try {
            login(httpRequest.params());
        } catch (IllegalArgumentException e) {
            return new HttpResponse("/401.html", HttpStatus.UNAUTHORIZED);
        }
        return new HttpResponse("/index.html", HttpStatus.FOUND);
    }

    private void login(Map<String, String> paramsMap) {
        User user = getValidatedUser(paramsMap);
        log.info("user: {}", user.toString());
    }

    private User getValidatedUser(Map<String, String> paramsMap) {
        User user = InMemoryUserRepository.findByAccount(paramsMap.get("account"))
                .orElseThrow(() -> {
                    log.info("로그인 실패: 조건을 만족하는 회원 없음");
                    return new IllegalArgumentException("회원 없음");
                });

        if (!user.checkPassword(paramsMap.get("password"))) {
            log.info("로그인 실패: 비밀번호 불일치");
            throw new IllegalArgumentException("비밀번호 불일치");
        }
        return user;
    }
}
