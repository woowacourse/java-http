package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.HttpConstants.COOKIE_JSESSIONID;
import static org.apache.coyote.http11.HttpConstants.EMPTY;
import static org.apache.coyote.http11.HttpConstants.GET_HTTP_METHOD;
import static org.apache.coyote.http11.HttpConstants.INDEX_PAGE;
import static org.apache.coyote.http11.HttpConstants.LOCATION_HEADER;
import static org.apache.coyote.http11.HttpConstants.SLASH;
import static org.apache.coyote.http11.HttpConstants.UNAUTHORIZED_PAGE;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.UUID;
import org.apache.coyote.http11.dto.HttpRequest;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(RegisterHandler.class);

    private final StaticFileHandler staticFileHandler;

    public RegisterHandler(final StaticFileHandler staticFileHandler) {
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
     * GET /register 요청 처리
     * - 회원가입 페이지 응답: register.html
     */
    private HandlerResult handleGetRequest(final HttpRequest request) {
        return staticFileHandler.doHandle(request);
    }

    /**
     * POST /register 요청 처리
     * - 회원가입 성공: index.html (redirect)
     * - 회원가입 실패: 401.html
     */
    private HandlerResult handlePostRequest(final HttpRequest request) {
        // 1. 쿼리 파라미터 추출
        final String account = getParam(request, "account");
        final String password = getParam(request, "password");
        final String email = getParam(request, "email");

        // 2. 회원가입 실패: 401.html - 파라미터 검증
        if (hasMissingCredentials(account, password, email)) {
            log.info("Register attempt with missing credentials");
            return handleUnauthorizedRequest(request);
        }

        // 3. 회원가입 실패: 401.html - 사용자 검증
        if (isUserAlreadyRegistered(account)) {
            log.warn("Register failed for account '{}' - already exists", account);
            return handleUnauthorizedRequest(request);
        }

        // 4. 회원가입 처리
        final User user = registerNewUser(account, password, email);

        // 5. 회원가입 성공: index.html (redirect)
        final Session session = createSession(user);
        return redirectTo(INDEX_PAGE, session.getId());
    }

    // 401 Unauthorized 처리
    private HandlerResult handleUnauthorizedRequest(final HttpRequest request) {
        final HttpRequest newRequest = new HttpRequest(
                request.method(), SLASH + UNAUTHORIZED_PAGE, request.query(), request.protocol(), request.headers(),
                request.httpCookie()
        );
        final HandlerResult result = staticFileHandler.doHandle(newRequest);

        return HandlerResult.builder()
                .status(Status.UNAUTHORIZED)
                .contentType(ContentType.HTML)
                .body(result.body())
                .build();
    }

    // 지정된 Page로 리다이렉트하는 HandlerResult 생성
    private HandlerResult redirectTo(final String page, final String sessionId) {
        return HandlerResult.builder()
                .status(Status.FOUND)
                .header(LOCATION_HEADER, SLASH + page)
                .cookie(COOKIE_JSESSIONID, sessionId)
                .build();
    }

    private User registerNewUser(final String account, final String password, final String email) {
        final User newUser = new User(account, password, email);
        InMemoryUserRepository.save(newUser);
        log.info("Register succeeded for account '{}'", account);
        return newUser;
    }

    private Session createSession(final User user) {
        final Session session = new Session(UUID.randomUUID().toString());
        log.info("New session '{}' created for user '{}'", session.getId(), user.getAccount());
        session.setAttribute("user", user);
        SessionManager.getInstance().add(session);
        return session;
    }

    // ==== 유효성 검증 헬퍼 메서드 ====
    private boolean hasMissingCredentials(final String account, final String password, final String email) {
        return account.isBlank() || password.isBlank() || email.isBlank();
    }

    private boolean isUserAlreadyRegistered(final String account) {
        return InMemoryUserRepository.findByAccount(account).isPresent();
    }

    // ==== 파라미터 추출 헬퍼 메서드 ====
    private String getParam(final HttpRequest request, final String key) {
        if (request.query() == null) {
            return EMPTY;
        }
        return request.query().getOrDefault(key, EMPTY).trim();
    }
}
