package nextstep.jwp.presentation;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.QueryParam;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    private static final String REDIRECT_URL = "/index.html";
    private static final String LOGIN_URL = "/login.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";


    @Override
    protected ResponseEntity doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (httpRequest.getUrl().startsWith("/login")) {
            return login(httpRequest, httpResponse);
        }
        return register(httpRequest, httpResponse);
    }

    @Override
    protected ResponseEntity doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        return new ResponseEntity(StatusCode.OK, LOGIN_URL);
    }

    private ResponseEntity login(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (httpRequest.hasJSESSIONID()) {
            final Session session = SessionManager.findSession(httpRequest.getJSESSIONID());
            if (session == null && httpRequest.getMethod().equals("POST")) {
                return requireAuthByRequestInfo(httpRequest, httpResponse);
            }
            if (session == null && httpRequest.getMethod().equals("GET")) {
                return new ResponseEntity(StatusCode.OK, LOGIN_URL);
            }
            if (session.hasAttribute("user")) {
                return new ResponseEntity(StatusCode.MOVED_TEMPORARILY, REDIRECT_URL)
                        .setCookie(session);
            }
        }

        if (httpRequest.getMethod().equals("GET")) {
            return new ResponseEntity(StatusCode.OK, LOGIN_URL);
        }
        return requireAuthByRequestInfo(httpRequest, httpResponse);
    }

    private ResponseEntity requireAuthByRequestInfo(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (QueryParam.isQueryParam(httpRequest.getUrl())) {
            final QueryParam queryParam = new QueryParam(httpRequest.getUrl());
            if (queryParam.matchParameters(ACCOUNT) && queryParam.matchParameters(PASSWORD)) {
                return authentication(queryParam.getValue(ACCOUNT), queryParam.getValue(PASSWORD));
            }
        }

        final String account = httpRequest.getBodyValue(ACCOUNT);
        final String password = httpRequest.getBodyValue(PASSWORD);
        return authentication(account, password);
    }

    private ResponseEntity authentication(final String account, final String password) {
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            LOGGER.info(user.get().toString());

            final Session session = SessionManager.add(HttpCookie.makeJSESSIONID());
            session.addAttribute("user", user.get());
            return new ResponseEntity(StatusCode.MOVED_TEMPORARILY, REDIRECT_URL).setCookie(session);
        }
        return new ResponseEntity(StatusCode.MOVED_TEMPORARILY, "/401.html");
    }

    private ResponseEntity register(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (httpRequest.getUrl().startsWith("/register") && httpRequest.getMethod().equals("POST")) {
            final String account = httpRequest.getBodyValue(ACCOUNT);
            final String password = httpRequest.getBodyValue(PASSWORD);
            final String email = httpRequest.getBodyValue("email");

            final User user = new User(account, password, email);
            InMemoryUserRepository.save(user);

            return new ResponseEntity(StatusCode.MOVED_TEMPORARILY, REDIRECT_URL);
        }

        return new ResponseEntity(StatusCode.OK, httpRequest.getUrl());
    }
}