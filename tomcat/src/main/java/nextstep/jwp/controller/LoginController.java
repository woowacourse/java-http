package nextstep.jwp.controller;

import java.util.Map;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.web.Cookie;
import org.apache.coyote.http11.web.Session;
import org.apache.coyote.http11.web.SessionManager;

public class LoginController extends Controller {
    @Override
    public void processPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final SessionManager sessionManager = new SessionManager();

        final Map<String, String> body = httpRequest.getBody();
        final User user = InMemoryUserRepository.findByAccount(body.get("account"))
                .orElseThrow();

        if (user.checkPassword(body.get("password"))) {
            final Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute(Cookie.ofJSessionId().toPair(), user);
            sessionManager.add(session);

            httpResponse.addCookie(Cookie.ofJSessionId());
            httpResponse.sendRedirect("/index.html");
            return;
        }
        httpResponse.sendRedirect("/404.html");
    }

    @Override
    public void processGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setView("login");
    }
}
