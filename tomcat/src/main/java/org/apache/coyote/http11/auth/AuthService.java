package org.apache.coyote.http11.auth;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.line.Protocol;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.response.ResponseEntity;

import static org.apache.coyote.http11.response.ResponsePage.CONFLICT_PAGE;
import static org.apache.coyote.http11.response.ResponsePage.INDEX_PAGE;
import static org.apache.coyote.http11.response.ResponsePage.LOGIN_PAGE;
import static org.apache.coyote.http11.response.ResponsePage.REGISTER_PAGE;
import static org.apache.coyote.http11.response.ResponsePage.UNAUTHORIZED_PAGE;
import static org.apache.coyote.http11.request.line.HttpMethod.GET;

public class AuthService {

    public static final String SESSION_KEY = "JSESSIONID";

    private final SessionRepository sessionRepository = new SessionRepository();

    public ResponseEntity login(RequestLine requestLine, RequestHeader requestHeader, RequestBody requestBody) {
        Protocol protocol = requestLine.protocol();
        if (requestLine.method().isGet()) {
            return getLoginOrIndexResponse(requestHeader, protocol);
        }
        final String account = requestBody.getBy("account");
        final String password = requestBody.getBy("password");
        return getLoginOrElseUnAuthorizedResponse(protocol, account, password);
    }

    private ResponseEntity getLoginOrIndexResponse(RequestHeader requestHeader, Protocol protocol) {
        final Cookie cookie = requestHeader.getCookie();
        final Session session = sessionRepository.getSession(cookie.get(SESSION_KEY));
        if (session == null) {
            return ResponseEntity.getCookieNullResponseEntity(protocol, LOGIN_PAGE);
        }
        return ResponseEntity.getCookieNullResponseEntity(protocol, INDEX_PAGE);
    }

    private ResponseEntity getLoginOrElseUnAuthorizedResponse(Protocol protocol, String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> getSuccessLoginResponse(user, protocol))
                .orElseGet(() -> ResponseEntity.getCookieNullResponseEntity(protocol, UNAUTHORIZED_PAGE));
    }

    private ResponseEntity getSuccessLoginResponse(final User user, final Protocol protocol) {
        ResponseEntity response = ResponseEntity.getCookieNullResponseEntity(protocol, INDEX_PAGE);
        String jsessionid = response.getHttpCookie().get(SESSION_KEY);
        final Session session = Session.from(jsessionid);
        session.setAttribute("user", user);
        sessionRepository.create(session);
        return response;
    }

    public ResponseEntity register(final RequestLine requestLine, final RequestBody requestBody) {
        Protocol protocol = requestLine.protocol();
        if (requestLine.method() == GET) {
            return ResponseEntity.getCookieNullResponseEntity(protocol, REGISTER_PAGE);
        }
        return getIndexOrConflictResponse(requestBody, protocol);

    }

    private static ResponseEntity getIndexOrConflictResponse(RequestBody requestBody, Protocol protocol) {
        final String account = requestBody.getBy("account");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return ResponseEntity.getCookieNullResponseEntity(protocol, CONFLICT_PAGE);
        }

        final String email = requestBody.getBy("email");
        final String password = requestBody.getBy("password");
        InMemoryUserRepository.save(new User(account, password, email));
        return ResponseEntity.getCookieNullResponseEntity(protocol, INDEX_PAGE);
    }

}
