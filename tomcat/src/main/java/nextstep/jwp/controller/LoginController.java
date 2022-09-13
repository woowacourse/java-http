package nextstep.jwp.controller;

import java.util.Map;
import java.util.Optional;
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

        final Map<String, String> body = httpRequest.getBody();
        final User user = InMemoryUserRepository.findByAccount(body.get("account"))
                .orElseThrow();

        if (user.checkPassword(body.get("password"))) {
            final Session session = httpRequest.getSession();
            session.setAttribute("user", user);

            httpResponse.addCookie(new Cookie("JSESSIONID", session.getId()));
            httpResponse.sendRedirect("/index.html");
            return;
        }
        httpResponse.sendRedirect("/401.html");
    }

    @Override
    public void processGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (!httpRequest.hasCookie()) {
            httpResponse.setStatus(HttpStatus.OK);
            httpResponse.setView("login");
            return;
        }
        final Cookie cookie = httpRequest.getCookie();
        final Optional<Session> session = SessionManager.findSession(cookie.getValue());
        if (session.isPresent()) {
            httpResponse.sendRedirect("/index.html");
        }
    }
}
