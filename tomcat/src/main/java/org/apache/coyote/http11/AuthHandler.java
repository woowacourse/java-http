package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthHandler {

    private static final Logger log = LoggerFactory.getLogger(AuthHandler.class);

    public static void authenticate(Map<String, String> authInfo) {
        User user = InMemoryUserRepository.findByAccount(authInfo.get("account"))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 정보입니다."));
        
        if (!user.checkPassword(authInfo.get("password"))) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        log.info("로그인 성공: {}", user);
    }

    public static void register(Map<String, String> registerInfo) {
        User user = new User(registerInfo.get("account"), registerInfo.get("password"), registerInfo.get("email"));
        InMemoryUserRepository.save(user);
        log.info("회원가입 성공: {}", user);
    }
}
