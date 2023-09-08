package org.apache.coyote.http11.auth;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.line.Protocol;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.response.Location;
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
    public static final String SESSION_KEY = "JSESSIONID";

    private final SessionRepository sessionRepository = new SessionRepository();

    public ResponseEntity login(RequestLine requestLine, RequestHeader requestHeader, RequestBody requestBody) {
        Protocol protocol = requestLine.protocol();
        if (requestLine.method().isGet()) {
            final Cookie cookie = requestHeader.getCookie();
            final Session session = sessionRepository.getSession(cookie.get(SESSION_KEY));
            if (session == null) {
                return ResponseEntity.getCookieNullResponseEntity(protocol, OK, Location.from(LOGIN_PAGE));
            }
            return ResponseEntity.getCookieNullResponseEntity(protocol, FOUND, Location.from(INDEX_PAGE));
        }

        final String account = requestBody.getBy("account");
        final String password = requestBody.getBy("password");
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> getSuccessLoginResponse(user, protocol))
                .orElseGet(() -> ResponseEntity.getCookieNullResponseEntity(protocol, UNAUTHORIZED,
                        Location.from(UNAUTHORIZED_PAGE)));
    }

    private ResponseEntity getSuccessLoginResponse(final User user, final Protocol protocol) {
        ResponseEntity response = ResponseEntity.getCookieNullResponseEntity(protocol, FOUND,
                Location.from(INDEX_PAGE));
        String jsessionid = response.getHttpCookie().get(SESSION_KEY);
        final Session session = Session.from(jsessionid);
        session.setAttribute("user", user);
        sessionRepository.create(session);
        return response;
    }

    public ResponseEntity register(final RequestLine requestLine, final RequestBody requestBody) {
        Protocol protocol = requestLine.protocol();
        if (requestLine.method() == GET) {
            return ResponseEntity.getCookieNullResponseEntity(protocol, OK, Location.from(REGISTER_PAGE));
        }

        final String account = requestBody.getBy("account");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return ResponseEntity.getCookieNullResponseEntity(protocol, CONFLICT, Location.from(CONFLICT_PAGE));
        }

        final String email = requestBody.getBy("email");
        final String password = requestBody.getBy("password");
        InMemoryUserRepository.save(new User(account, password, email));
        return ResponseEntity.getCookieNullResponseEntity(protocol, FOUND, Location.from(INDEX_PAGE));
    }

}
