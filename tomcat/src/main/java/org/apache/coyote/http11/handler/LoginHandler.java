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
        if (hasMissingCredentials(account, password)) {
            log.info("Login attempt with missing params. "
                    + "accountPresent={}, passwordPresent={}", !account.isEmpty(), !password.isEmpty());
            return handleUnauthorizedLogin(request);
        }

        // 3. 사용자 조회
        final Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (isUserNotFound(optionalUser)) {
            log.info("Login failed: unknown account '{}'", account);
            return handleUnauthorizedLogin(request);
        }

        // 4. 비밀번호 검증
        final User user = optionalUser.get();
        if (isPasswordMismatch(user, password)) {
            log.info("Login failed: wrong password for account '{}'", account);
            return handleUnauthorizedLogin(request);
        }

        // 5. 로그인 성공
        log.info("Login succeeded for account '{}'", account);
        return handleLoginSuccess(request);
    }

    private HandlerResult handleUnauthorizedLogin(final HttpRequest request) {
        final HttpRequest loginFailRequest = redirectHttpRequest(request, "401.html");
        final HandlerResult loginFail = staticFileHandler.doHandle(loginFailRequest);
        return HandlerResult.unauthorized(loginFail.contentType(), loginFail.body());
    }

    // 로그인 처리
    private HandlerResult handleLoginSuccess(final HttpRequest request) {
        final HttpRequest loginSuccessRequest = redirectHttpRequest(request, "index.html");
        final HandlerResult loginSuccess = staticFileHandler.doHandle(loginSuccessRequest);
        return HandlerResult.found(loginSuccess.contentType(), loginSuccess.body());
    }

    private HttpRequest redirectHttpRequest(final HttpRequest request, final String route) {
        return new HttpRequest(request.method(), route, request.query(), request.protocol(), request.headers());
    }

    // 로그인 유효성 검증
    private boolean hasMissingCredentials(final String account, final String password) {
        return account.isEmpty() || password.isEmpty();
    }

    private boolean isUserNotFound(final Optional<User> optionalUser) {
        return optionalUser.isEmpty();
    }

    private boolean isPasswordMismatch(final User user, final String password) {
        return !user.checkPassword(password);
    }

    // 헬퍼 메서드
    private boolean isBlank(final String value) {
        return value == null || value.trim().isEmpty();
    }

    private String getParam(final HttpRequest request, final String key) {
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
