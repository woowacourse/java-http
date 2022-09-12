package nextstep.jwp.ui;

import static org.apache.coyote.http11.response.StatusCode.FOUND;
import static org.apache.coyote.http11.response.StatusCode.OK;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11QueryParams;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.support.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String LOGIN_HTML = "/login.html";
    private static final String INDEX_HTML = "/index.html";

    @Override
    protected Http11Response doGet(final Http11Request request) {
        if (request.hasNoJsessionIdCookie()) {
            return Http11Response.of(OK, LOGIN_HTML);
        }
        return Http11Response.of(FOUND, LOGIN_HTML)
                .addHeader("Location", INDEX_HTML);
    }

    @Override
    protected Http11Response doPost(final Http11Request request) {
        final List<String> loginInfo = List.of("account", "password");
        final Http11QueryParams queryParams = Http11QueryParams.from(request.getRequestBody());
        if (queryParams.hasQueryParams(loginInfo) && successLogin(request)) {
            final User user = getUser(queryParams);
            log.info(user.toString());

            final UUID uuid = UUID.randomUUID();
            startSession(user, uuid);
            final HttpCookie cookie = new HttpCookie(Map.of("JSESSIONID", uuid.toString()));

            return Http11Response.of(FOUND, LOGIN_HTML)
                    .addHeader("Location", INDEX_HTML)
                    .addHeader("Set-Cookie", cookie.cookieToString("JSESSIONID"));
        }
        return Http11Response.of(FOUND, LOGIN_HTML)
                .addHeader("Location", "/401.html");
    }

    private boolean successLogin(final Http11Request request) {
        final Http11QueryParams queryParams = Http11QueryParams.from(request.getRequestBody());
        final String account = queryParams.getValueFrom("account");
        final String password = queryParams.getValueFrom("password");

        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        return user.isPresent() && user.get().checkPassword(password);
    }

    private User getUser(final Http11QueryParams queryParams) {
        final String account = queryParams.getValueFrom("account");
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NotFoundUserException::new);
    }

    private void startSession(final User user, final UUID uuid) {
        final Session session = new Session(uuid);
        session.setAttribute("user", user);
        SessionManager.add(session);
    }
}
