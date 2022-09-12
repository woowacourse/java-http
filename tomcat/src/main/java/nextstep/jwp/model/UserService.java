package nextstep.jwp.model;

import static nextstep.jwp.exception.ExceptionType.INVALID_HTTP_LOGIN_EXCEPTION;
import static nextstep.jwp.exception.ExceptionType.INVALID_HTTP_REGISTER_EXCEPTION;
import static nextstep.jwp.exception.ExceptionType.MISS_MATCH_USER_PASSWORD_EXCEPTION;
import static nextstep.jwp.exception.ExceptionType.NOT_FOUND_USER_EXCEPTION;

import java.util.Map;
import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidHttpRequestException;
import org.apache.coyote.http11.common.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final UserService INSTANCE = new UserService();
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private static final String SUCCEED_REDIRECT_URL = "/index.html";
    private static final String ID = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static Long autoIncrementCount = 1L;

    private UserService() {
    }

    public void validateUserBySession(HttpRequest request) {
        SessionManager.findSession(request.getCookie());
    }

    public void login(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final Map<String, String> params = httpRequest.getParams();
        validateLoginParams(params);

        final User user = findUser(params);
        httpResponse.redirectWithCookie(user, SUCCEED_REDIRECT_URL);
    }

    private void validateLoginParams(final Map<String, String> params) {
        if (params.isEmpty()) {
            throw new InvalidHttpRequestException(INVALID_HTTP_LOGIN_EXCEPTION);
        }
    }

    private User findUser(Map<String, String> params) {
        final User user = InMemoryUserRepository.findByAccount(params.get(ID))
                .orElseThrow(() -> new NoSuchElementException(NOT_FOUND_USER_EXCEPTION.getMessage()));

        if (!user.checkPassword(params.get(PASSWORD))) {
            throw new IllegalArgumentException(MISS_MATCH_USER_PASSWORD_EXCEPTION.getMessage());
        }

        loggingInfoUser(params, user);
        return user;
    }

    public User register(Map<String, String> params) {
        validateResister(params);
        User user = getUser(params);
        InMemoryUserRepository.save(user);
        return user;
    }

    private void validateResister(final Map<String, String> params) {
        if (isValidRegister(params)) {
            throw new InvalidHttpRequestException(INVALID_HTTP_REGISTER_EXCEPTION);
        }
    }

    private boolean isValidRegister(final Map<String, String> params) {
        return params.isEmpty() || !params.containsKey(ID) || !params.containsKey(PASSWORD) || !params.containsKey(
                EMAIL);
    }

    private User getUser(Map<String, String> params) {
        return new User(++autoIncrementCount, params.get(ID), params.get(PASSWORD), params.get(EMAIL));
    }

    private void loggingInfoUser(Map<String, String> params, User user) {
        if (user.checkPassword(params.get(PASSWORD))) {
            log.info(" 로그인 성공! 아이디 : " + user.getAccount());
        }
    }

    public static UserService getInstance() {
        return INSTANCE;
    }
}
