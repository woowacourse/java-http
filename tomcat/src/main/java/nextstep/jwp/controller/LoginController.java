package nextstep.jwp.controller;

import nextstep.jwp.AbstractController;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.exception.UnsupportedMethodException;
import nextstep.jwp.model.User;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.body.Body;
import org.apache.coyote.http11.header.Cookie;
import org.apache.coyote.http11.header.HttpStatus;

public class LoginController extends AbstractController {
    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final Body body = request.body();
        final String account = body.getValue("account");
        final String password = body.getValue("password");
        final User loginUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .orElseThrow(() -> new AuthenticationException("아이디 또는 비밀번호가 틀립니다."));
        final SessionManager sessionManager = SessionManager.getInstance();
        final String sessionId = sessionManager.createSession(loginUser);
        response.addStatus(HttpStatus.FOUND)
                .addLocation("/index.html")
                .addSetCookie(new Cookie("JSESSIONID", sessionId));
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.headers().getCookies().containsKey("JSESSIONID")) {
            response.addStatus(HttpStatus.FOUND)
                    .addLocation("/index.html");
            return;
        }
        response.addStatus(HttpStatus.FOUND)
                .addLocation("/login.html");
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.isGet()) {
            doGet(request, response);
            return;
        }
        if (request.isPost()) {
            doPost(request, response);
            return;
        }
        throw new UnsupportedMethodException();
    }
}
