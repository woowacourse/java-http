package org.apache.coyote.http11.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.coyote.http11.dto.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
    private final StaticFileHandler staticFileHandler;

    public LoginHandler(final StaticFileHandler staticFileHandler) {
        this.staticFileHandler = staticFileHandler;
    }

    @Override
    public HandlerResult doHandle(final HttpRequest request) {
        // 1, 로그인 페이지 반환 (파라미터 없는 GET /login)
        if ("GET".equalsIgnoreCase(request.method())
                && isBlank(getParam(request, "account"))
                && isBlank(getParam(request, "password"))
        ) {
            return staticFileHandler.doHandle(request);
        }

        // 2. 로그인 페이지 반환 (파라미터 있는 GET /login)
        return tryServeWithParams(request);
    }

    private HandlerResult tryServeWithParams(final HttpRequest request) {
        // 1. 쿼리 파라미터 추출
        final String account = getParam(request, "account");
        final String password = getParam(request, "password");

        // 2. 파라미터 검증
        if (account.isEmpty() || password.isEmpty()) {
            log.debug("Login attempt with missing parameters. accountPresent={}, passwordPresent={}",
                    !account.isEmpty(), !password.isEmpty());
            return HandlerResult.badRequest("Both 'account' and 'password' parameters are required.");
        }

        // 3. 사용자 조회
        final Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isEmpty()) {
            log.info("Login failed: unknown account '{}'", account);
            return HandlerResult.badRequest("Invalid user account");
        }

        // 4. 비밀번호 검증
        final User user = optionalUser.get();
        if (!user.checkPassword(password)) {
            log.info("Login failed: wrong password for account '{}'", account);
            return HandlerResult.badRequest("Invalid password of account.");
        }

        // 5. 로그인 성공
        log.info("Login succeeded for account '{}'", account);
        return staticFileHandler.doHandle(request);
    }

    private boolean isBlank(final String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String getParam(final HttpRequest request, final String key) {
        if (request.query() == null) {
            return null;
        }

        final String value = request.query().get(key);
        if (value == null) {
            return null;
        }
        return value.trim();
    }
}
