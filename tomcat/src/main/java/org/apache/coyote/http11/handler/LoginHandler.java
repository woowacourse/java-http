package org.apache.coyote.http11.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.coyote.http11.dto.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
    private static final String SLASH = "/";
    private static final String GET_METHOD = "GET";
    private static final String UNAUTHORIZED_PAGE = "401.html";
    private static final String SUCCESS_PAGE = "index.html";

    private final StaticFileHandler staticFileHandler;

    public LoginHandler(final StaticFileHandler staticFileHandler) {
        this.staticFileHandler = staticFileHandler;
    }

    @Override
    public HandlerResult doHandle(final HttpRequest request) {
        // 1, GET /login (returns login.html)
        if (GET_METHOD.equalsIgnoreCase(request.method())) {
            return staticFileHandler.doHandle(request);
        }

        // 2. POST /login [with params] (returns index.html or 401.html)
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
            return handleUnauthorizedRequest(request);
        }

        // 3. 사용자 조회
        final Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (isUserNotFound(optionalUser)) {
            log.info("Login failed: unknown account '{}'", account);
            return handleUnauthorizedRequest(request);
        }

        // 4. 비밀번호 검증
        final User user = optionalUser.get();
        if (isPasswordMismatch(user, password)) {
            log.info("Login failed: wrong password for account '{}'", account);
            return handleUnauthorizedRequest(request);
        }

        // 5. 로그인 처리
        login(account, password);

        // 6. 로그인 성공
        return handleRequestSuccess(request);
    }

    // == 비즈니스 로직 처리 ==
    private void login(final String account, final String password) {
        log.info("Login succeeded for account '{}'", account);
    }

    // == 요청 처리 ==
    private HandlerResult handleUnauthorizedRequest(final HttpRequest request) {
        final HttpRequest failRequest = redirectHttpRequest(request, SLASH + UNAUTHORIZED_PAGE);
        final HandlerResult fail = staticFileHandler.doHandle(failRequest);
        return HandlerResult.unauthorized(fail.mimeType(), fail.body());
    }

    private HandlerResult handleRequestSuccess(final HttpRequest request) {
        final HttpRequest successRequest = redirectHttpRequest(request, SLASH + SUCCESS_PAGE);
        final HandlerResult success = staticFileHandler.doHandle(successRequest);
        final HandlerResult result = HandlerResult.redirectFound(success.mimeType(), success.body());
        result.addHeader("Location", SLASH + SUCCESS_PAGE);
        return result;
    }

    private HttpRequest redirectHttpRequest(final HttpRequest request, final String route) {
        return new HttpRequest(request.method(), route, request.query(), request.protocol(), request.headers(),
                request.httpCookie());
    }

    // == 요청 유효성 검증 ==
    private boolean hasMissingCredentials(final String account, final String password) {
        return account == null || account.isEmpty() || password == null || password.isEmpty();
    }

    private boolean isUserNotFound(final Optional<User> optionalUser) {
        return optionalUser.isEmpty();
    }

    private boolean isPasswordMismatch(final User user, final String password) {
        return !user.checkPassword(password);
    }

    // == 헬퍼 메서드 ==
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
