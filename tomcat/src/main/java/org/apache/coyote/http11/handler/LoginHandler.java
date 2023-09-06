package org.apache.coyote.http11.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.common.SessionManager;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.coyote.http11.common.HttpStatus.FOUND;

public class LoginHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    private static final String LOGIN_ACCOUNT = "account";

    private static final String LOGIN_PASSWORD = "password";

    private static final String HEADER_COOKIE = "Cookie";

    private static final String SESSION_COOKIE_NAME = "JSESSIONID";

    private LoginHandler() {
    }

    public static HttpResponse handle(final RequestLine requestLine, final RequestHeader requestHeader, final RequestBody requestBody) {
        if (requestLine.getRequestMethod().equals("GET")) {
            if (isAuthenticated(requestHeader)) {
                return new HttpResponseBuilder().init()
                        .httpStatus(FOUND)
                        .header("Location: /index.html ")
                        .build();
            }
            return StaticFileHandler.handle("/login.html", requestHeader);
        }
        return login(requestBody);
    }

    private static boolean isAuthenticated(final RequestHeader requestHeader) {
        HttpCookie cookie = HttpCookie.from(requestHeader.getHeaderValue(HEADER_COOKIE));
        String jsessionId = cookie.getCookieValue(SESSION_COOKIE_NAME);
        return jsessionId != null && SessionManager.findSession(jsessionId) != null;
    }

    private static HttpResponse login(final RequestBody requestBody) {
        try {
            String account = requestBody.getContentValue(LOGIN_ACCOUNT);
            String password = requestBody.getContentValue(LOGIN_PASSWORD);
            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException()); //todo : 해당하는 계정이 존재하지 않는다
            if (!user.checkPassword(password)) {
                throw new RuntimeException(); //todo :비밀번호가 일치하지 않는다.
            }
            return successToLogin(user);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return failToLogin();
        }
    }

    private static Session createSession(final User user) {
        Session session = new Session();
        session.setAttribute("user", user);
        SessionManager.addSession(session);
        return session;
    }

    private static HttpResponse successToLogin(User user) {
        Session session = createSession(user);
        return new HttpResponseBuilder().init()
                .httpStatus(FOUND)
                .header("Set-Cookie: JSESSIONID=" + session.getId() + " ")
                .header("Location: /index.html ")
                .build();
    }

    private static HttpResponse failToLogin() {
        return new HttpResponseBuilder().init()
                .httpStatus(FOUND)
                .header("Location: /401.html ")
                .build();
    }

}
