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

import static org.apache.coyote.http11.common.HttpStatus.REDIRECTION;

public class LoginHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    public static HttpResponse handle(final RequestLine requestLine, final RequestHeader requestHeader, final RequestBody requestBody) {
        if (requestLine.getRequestMethod().equals("GET")) {
            HttpCookie cookie = HttpCookie.from(requestHeader.getHeaderValue("Cookie"));
            String jseessionid = cookie.getCookieValue("JSESSIONID");
            if (jseessionid != null && SessionManager.findSession(jseessionid) != null) {
                return new HttpResponseBuilder().init()
                        .httpStatus(REDIRECTION)
                        .header("Location: /index.html ")
                        .build();
            }
            return StaticFileHandler.handle("/login.html", requestHeader);
        }
        return login(requestBody);
    }

    private static HttpResponse login(final RequestBody requestBody) {
        try {
            String account = requestBody.getContentValue("account");
            String password = requestBody.getContentValue("password");
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
                .httpStatus(REDIRECTION)
                .header("Set-Cookie: JSESSIONID=" + session.getId() + " ")
                .header("Location: /index.html ")
                .build();
    }

    private static HttpResponse failToLogin() {
        return new HttpResponseBuilder().init()
                .httpStatus(REDIRECTION)
                .header("Location: /401.html ")
                .build();
    }

}
