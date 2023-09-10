package org.apache.coyote.http11.auth;

import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.line.Protocol;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;

import static org.apache.coyote.http11.response.ResponsePage.INDEX_PAGE;
import static org.apache.coyote.http11.response.ResponsePage.LOGIN_PAGE;
import static org.apache.coyote.http11.response.ResponsePage.UNAUTHORIZED_PAGE;

public class LoginService {

    public static final String COOKIE_KEY = "JSESSIONID";

    private final SessionRepository sessionRepository = new SessionRepository();

    public HttpResponse login(RequestLine requestLine, RequestHeader requestHeader, RequestBody requestBody) {
        Protocol protocol = requestLine.protocol();
        if (requestLine.method().isGet()) {
            return getLoginOrIndexResponse(requestHeader, protocol);
        }
        final String account = requestBody.getBy("account");
        final String password = requestBody.getBy("password");
        return getLoginOrElseUnAuthorizedResponse(protocol, account, password);
    }

    private HttpResponse getLoginOrIndexResponse(RequestHeader requestHeader, Protocol protocol) {
        final Cookie cookie = requestHeader.getCookie();
        final Session session = sessionRepository.getSession(cookie.get(COOKIE_KEY));
        if (session == null) {
            System.out.println("세션 없음 로그인 페이지로 이동");
            return HttpResponse.getCookieNullResponseEntity(protocol, LOGIN_PAGE);
        }
        System.out.println("세션 존재 인덱스 페이지로 이동");
        return HttpResponse.getCookieNullResponseEntity(protocol, INDEX_PAGE);
    }

    private HttpResponse getLoginOrElseUnAuthorizedResponse(Protocol protocol, String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> getSuccessLoginResponse(user, protocol))
                .orElseGet(() -> HttpResponse.getCookieNullResponseEntity(protocol, UNAUTHORIZED_PAGE));
    }

    private HttpResponse getSuccessLoginResponse(final User user, final Protocol protocol) {
        final String uuid = UUID.randomUUID().toString();
        final Session session = Session.from(uuid);
        session.setAttribute("user", user);
        sessionRepository.create(session);
        Cookie cookie = new Cookie();
        cookie.setSession(session);
        return HttpResponse.getResponseEntity(protocol, cookie, INDEX_PAGE);
    }

}
