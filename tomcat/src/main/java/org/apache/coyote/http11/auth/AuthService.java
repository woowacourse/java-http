package org.apache.coyote.http11.auth;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.request.header.RequestHeader;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.response.ResponseEntity;

import static org.apache.coyote.http11.request.line.HttpMethod.GET;
import static org.apache.coyote.http11.response.HttpStatus.CONFLICT;
import static org.apache.coyote.http11.response.HttpStatus.FOUND;
import static org.apache.coyote.http11.response.HttpStatus.OK;
import static org.apache.coyote.http11.response.HttpStatus.UNAUTHORIZED;

public class AuthService {

    private static final String INDEX_PAGE = "/index.html";
    private static final String REGISTER_PAGE = "/register.html";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String CONFLICT_PAGE = "/409.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String COOKIE_NAME = "JSESSIONID";

    private final SessionRepository sessionRepository = new SessionRepository();

    public ResponseEntity login(RequestLine requestLine, RequestHeader requestHeader, RequestBody requestBody) {
        if (requestLine.method().isGet()) {
            final Cookie cookie = requestHeader.getCookie();
            final Session session = sessionRepository.getSession(cookie.get(COOKIE_NAME));
            if (session == null) {
                return new ResponseEntity(OK, LOGIN_PAGE);
            }
            String account = requestBody.getBy("account");
            if (InMemoryUserRepository.findByAccount(account).isPresent()) {
                return new ResponseEntity(FOUND, INDEX_PAGE);
            }
            throw new IllegalArgumentException("쿠키 또는 세션에 문제가 있습니다. 쿠키와 세션을 제거하고 다시 접근해주세요.");
        }

        final String account = requestBody.getBy("account");
        final String password = requestBody.getBy("password");
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(this::getSuccessLoginResponse)
                .orElseGet(() -> new ResponseEntity(UNAUTHORIZED, UNAUTHORIZED_PAGE));
    }

    private ResponseEntity getSuccessLoginResponse(final User user) {
        ResponseEntity response = ResponseEntity.getCookieResponseEntity(FOUND, INDEX_PAGE);
        String jsessionid = response.getHttpCookie().get("JSESSIONID");
        final Session session = Session.from(jsessionid);
        session.setAttribute("user", user);
        sessionRepository.create(session);
        return response;
    }

    public ResponseEntity register(final RequestLine requestLine, final RequestBody requestBody) {
        if (requestLine.method() == GET) {
            return new ResponseEntity(OK, REGISTER_PAGE);
        }

        final String account = requestBody.getBy("account");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return new ResponseEntity(CONFLICT, CONFLICT_PAGE);
        }

        final String email = requestBody.getBy("email");
        final String password = requestBody.getBy("password");
        InMemoryUserRepository.save(new User(account, password, email));
        return new ResponseEntity(FOUND, INDEX_PAGE);
    }

}
