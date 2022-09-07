package nextstep.jwp;

import static org.apache.coyote.http11.StatusCode.FOUND;
import static org.apache.coyote.http11.StatusCode.OK;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11QueryParams;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements Function<Http11Request, Http11Response> {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public Http11Response apply(final Http11Request request) {
        if (request.isGetMethod()) {
            return getLoginPageResponse(request);
        }

        List<String> loginInfo = List.of("account", "password");
        final Http11QueryParams queryParams = Http11QueryParams.from(request.getRequestBody());
        if (request.isPostMethod() && queryParams.hasQueryParams(loginInfo) && successLogin(request)) {
            User user = getUser(queryParams);
            log.info(user.toString());

            UUID uuid = UUID.randomUUID();
            startSession(user, uuid);
            HttpCookie cookie = new HttpCookie(Map.of("JSESSIONID", uuid.toString()));
            return Http11Response.withLocationAndSetJsessionIdCookie(FOUND, "/login.html", "/index.html", cookie);
        }
        return Http11Response.withLocation(FOUND, "/login.html", "/401.html");
    }

    private Http11Response getLoginPageResponse(final Http11Request request) {
        if (request.hasNoJsessionIdCookie()) {
            return Http11Response.of(OK, "/login.html");
        }
        return Http11Response.withLocation(FOUND, "/login.html", "/index.html");
    }

    private boolean successLogin(final Http11Request request) {
        final Http11QueryParams queryParams = Http11QueryParams.from(request.getRequestBody());
        final String account = queryParams.getValueFrom("account");
        final String password = queryParams.getValueFrom("password");

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        return user.isPresent() && user.get().checkPassword(password);
    }

    private static User getUser(final Http11QueryParams queryParams) {
        final String account = queryParams.getValueFrom("account");
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NotFoundUserException::new);
    }

    private static void startSession(final User user, final UUID uuid) {
        Session session = new Session(uuid);
        session.setAttribute("user", user);
        SessionManager.add(session);
    }
}
