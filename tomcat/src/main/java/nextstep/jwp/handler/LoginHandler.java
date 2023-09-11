package nextstep.jwp.handler;

import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.body.FormData;
import org.apache.coyote.http11.handler.FileHandler;
import org.apache.coyote.http11.handler.GetAndPostHandler;
import org.apache.coyote.http11.header.Cookies;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class LoginHandler extends GetAndPostHandler {

    private static final String SESSION_KEY = "JSESSIONID";
    private static final String UNAUTHORIZED_LOCATION = "/401";
    private static final String MAIN_LOCATION = "/index";
    private static final String SESSION_USER_KEY = "user";

    private final FileHandler fileHandler = new FileHandler();

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        Session session = getSessionFrom(httpRequest.getCookies());
        if (session != null && hasUserOn(session)) {
            httpResponse.redirectTo(MAIN_LOCATION);
        }
        fileHandler.handle(httpRequest, httpResponse);
    }

    private Session getSessionFrom(final Cookies cookies) {
        String sessionId = cookies.get(SESSION_KEY);
        if (sessionId == null) {
            return null;
        }
        return SessionManager.findSession(sessionId);
    }

    private boolean hasUserOn(Session session) {
        return Objects.nonNull(session.getAttribute(SESSION_USER_KEY));
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        FormData formData = FormData.of(httpRequest.getBody());
        String account = formData.get("account");
        String password = formData.get("password");

        Optional<User> found = InMemoryUserRepository.findByAccount(account);
        if (found.isPresent()) {
            User user = found.get();
            signIn(httpResponse, user, password);
            return;
        }
        httpResponse.redirectTo(UNAUTHORIZED_LOCATION);
    }

    private void signIn(final HttpResponse httpResponse, final User user, final String password) {
        if (user.checkPassword(password)) {
            Session session = new Session();
            session.addAttribute(SESSION_USER_KEY, user);
            SessionManager.add(session);

            Cookies cookies = new Cookies();
            cookies.add(SESSION_KEY, session.getId());

            httpResponse.with(cookies).redirectTo(MAIN_LOCATION);
            return;
        }
        httpResponse.redirectTo(UNAUTHORIZED_LOCATION);
    }
}
