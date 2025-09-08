package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.HttpConstants.COOKIE_JSESSIONID;
import static org.apache.coyote.http11.HttpConstants.DEFAULT_PROTOCOL;
import static org.apache.coyote.http11.HttpConstants.EMPTY;
import static org.apache.coyote.http11.HttpConstants.GET_HTTP_METHOD;
import static org.apache.coyote.http11.HttpConstants.INDEX_PAGE;
import static org.apache.coyote.http11.HttpConstants.LOCATION_HEADER;
import static org.apache.coyote.http11.HttpConstants.LOGIN_PAGE;
import static org.apache.coyote.http11.HttpConstants.SLASH;
import static org.apache.coyote.http11.HttpConstants.UNAUTHORIZED_PAGE;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.http11.dto.HttpCookie;
import org.apache.coyote.http11.dto.HttpRequest;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
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
        if (GET_HTTP_METHOD.equalsIgnoreCase(request.method())) {
            return handleGetRequest(request);
        }
        return handlePostRequest(request);
    }

    /**
     * GET /login 요청 처리
     * - 로그인된 상태: index.html (redirect)
     * - 로그인되지 않은 상태: login.html
     */
    private HandlerResult handleGetRequest(final HttpRequest request) {
        final Session session = findSession(request);
        // 로그인된 상태: index.html (redirect)
        if (session != null) {
            final User user = SessionManager.getInstance().getUser(session);
            log.info("User '{}' already logged in with session '{}'", user.getAccount(), session.getId());
            return redirectTo(INDEX_PAGE, session.getId());
        }

        // 로그인 되지 않은 상태: login.html
        final HttpRequest loginPageRequest = new HttpRequest(
                request.method(), SLASH + LOGIN_PAGE, request.query(), request.protocol(), request.headers(),
                request.httpCookie()
        );
        return staticFileHandler.doHandle(loginPageRequest);
    }

    /**
     * POST /login 요청 처리
     * - 로그인 성공: index.html (redirect)
     * - 로그인 실패: 401.html
     */
    private HandlerResult handlePostRequest(final HttpRequest request) {
        // 1. 쿼리 파라미터 추출
        final String account = extractParam(request, "account");
        final String password = extractParam(request, "password");

        // 2. 로그인 실패: 401.html - 파라미터 검증
        if (hasMissingCredentials(account, password)) {
            log.info("Login attempt with missing credentials");
            return handleUnauthorizedRequest(request);
        }

        // 3. 로그인 실패: 401.html - 사용자 검증
        final Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isEmpty()) {
            log.info("Login failed for account '{}'- doesn't exist", account);
            return handleUnauthorizedRequest(request);
        }

        // 3. 로그인 실패: 401.html - 비밀번호 검증
        final User user = optionalUser.get();
        if (isPasswordMismatch(user, password)) {
            log.info("Login failed for account '{}'- wrong password", account);
            return handleUnauthorizedRequest(request);
        }
        // 5. 로그인 처리
        loginUser(user, password);

        // 6. 로그인 성공: index.html (redirect)
        final Session session = getOrCreateSession(request, user);
        return redirectTo(INDEX_PAGE, session.getId());
    }

    // 401 Unauthorized 처리
    private HandlerResult handleUnauthorizedRequest(final HttpRequest request) {
        final HttpRequest newRequest = new HttpRequest(
                request.method(), SLASH + UNAUTHORIZED_PAGE, request.query(), request.protocol(), request.headers(), request.httpCookie()
        );
        final HandlerResult result = staticFileHandler.doHandle(newRequest);

        return HandlerResult.builder()
                .status(Status.UNAUTHORIZED)
                .contentType(ContentType.HTML)
                .body(result.body())
                .build();
    }

    private void loginUser(final User user, final String password) {
        if (user.checkPassword(password)) {
            log.info("Login succeeded for account '{}'", user.getAccount());
        }
    }

    // 지정된 Page로 리다이렉트하는 HandlerResult 생성
    private HandlerResult redirectTo(final String page, final String sessionId) {
        return HandlerResult.builder()
                .status(Status.FOUND)
                .header(LOCATION_HEADER, SLASH + page)
                .cookie(COOKIE_JSESSIONID, sessionId)
                .build();
    }

    private Session findSession(final HttpRequest request) {
        final String sessionId = request.httpCookie().getCookie(COOKIE_JSESSIONID);
        if (sessionId == null) {
            return null;
        }
        return SessionManager.getInstance().findSession(sessionId);
    }

    private Session getOrCreateSession(final HttpRequest request, final User user) {
        Session session = findSession(request);

        if (session == null) {
            session = new Session(UUID.randomUUID().toString());
            log.info("New session '{}' created for user '{}'", session.getId(), user.getAccount());
        }

        session.setAttribute("user", user);
        SessionManager.getInstance().add(session);
        return session;
    }

    // ==== 유효성 검증 헬퍼 메서드 ====
    private boolean hasMissingCredentials(final String account, final String password) {
        return account == null || account.isBlank() || password == null || password.isBlank();
    }

    private boolean isPasswordMismatch(final User user, final String password) {
        return !user.checkPassword(password);
    }

    // ==== 파라미터 추출 헬퍼 메서드 ====
    private String extractParam(final HttpRequest request, final String key) {
        if (request.query() == null) {
            return EMPTY;
        }
        return request.query().getOrDefault(key, EMPTY).trim();
    }
}
