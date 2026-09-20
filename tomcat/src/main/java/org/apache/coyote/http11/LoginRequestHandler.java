package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LoginRequestHandler implements RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);

    @Override
    public String handle(Map<String, String> paramsMap) {
        try {
            login(paramsMap);
        } catch (IllegalArgumentException e){
            return "/401.html";
        }
        return "/index.html";
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
