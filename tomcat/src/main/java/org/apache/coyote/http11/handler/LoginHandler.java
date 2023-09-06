package org.apache.coyote.http11.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.common.SessionManager;
import org.apache.coyote.http11.exception.MissMatchPasswordException;
import org.apache.coyote.http11.exception.NotExistAccountException;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.coyote.http11.common.HttpStatus.FOUND;
import static org.apache.coyote.http11.common.constant.SessionConstant.SESSION_COOKIE_NAME;

public class LoginHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

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
        HttpCookie cookie = HttpCookie.from(requestHeader.getHeaderValue("Cookie"));
        String jsessionId = cookie.getCookieValue(SESSION_COOKIE_NAME);
        return jsessionId != null && SessionManager.findSession(jsessionId) != null;
    }

    private static HttpResponse login(final RequestBody requestBody) {
        try {
            String account = requestBody.getContentValue("account");
            String password = requestBody.getContentValue("password");
            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(NotExistAccountException::new);
            if (!user.checkPassword(password)) {
                throw new MissMatchPasswordException();
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
