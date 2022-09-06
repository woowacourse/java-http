package nextstep.jwp.presentation;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.QueryParam;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthController implements Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    private static final String REDIRECT_URL = "/index.html";
    private static final String LOGIN_URL = "/login.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    @Override
    public ResponseEntity run(final HttpHeader httpHeader, final HttpBody httpBody) {
        if (httpHeader.getUrl().startsWith("/login")) {
            return login(httpHeader, httpBody);
        }
        return register(httpHeader, httpBody);
    }

    private ResponseEntity login(final HttpHeader httpHeader, final HttpBody httpBody) {
        if (httpHeader.hasJSESSIONID()) {
            final Session session = SessionManager.findSession(httpHeader.getJSESSIONID());
            if (session == null && httpHeader.getMethod().equals("POST")) {
                return requireAuthByRequestInfo(httpHeader, httpBody);
            }
            if (session == null && httpHeader.getMethod().equals("GET")) {
                return new ResponseEntity(StatusCode.OK, LOGIN_URL);
            }
            if (session.hasAttribute("user")) {
                return new ResponseEntity(StatusCode.MOVED_TEMPORARILY, REDIRECT_URL).setCookie(session);
            }
        }

        if (httpHeader.getMethod().equals("GET")) {
            return new ResponseEntity(StatusCode.OK, LOGIN_URL);
        }
        return requireAuthByRequestInfo(httpHeader, httpBody);
    }

    private ResponseEntity requireAuthByRequestInfo(final HttpHeader httpHeader, final HttpBody httpBody) {
        if (QueryParam.isQueryParam(httpHeader.getUrl())) {
            final QueryParam queryParam = new QueryParam(httpHeader.getUrl());
            if (queryParam.matchParameters(ACCOUNT) && queryParam.matchParameters(PASSWORD)) {
                return authentication(queryParam.getValue(ACCOUNT), queryParam.getValue(PASSWORD));
            }
        }

        final String account = httpBody.getValue(ACCOUNT);
        final String password = httpBody.getValue(PASSWORD);
        return authentication(account, password);
    }

    private ResponseEntity authentication(final String account, final String password) {
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            LOGGER.info(user.get().toString());

            final Session session = SessionManager.add(HttpCookie.makeJSESSIONID());
            session.addAttribute("user", user);
            return new ResponseEntity(StatusCode.MOVED_TEMPORARILY, REDIRECT_URL).setCookie(session);
        }
        return new ResponseEntity(StatusCode.UNAUTHORIZED, "/401.html");
    }

    private ResponseEntity register(final HttpHeader httpHeader, final HttpBody httpBody) {
        if (httpHeader.getUrl().startsWith("/register") && httpHeader.getMethod().equals("POST")) {
            final String account = httpBody.getValue(ACCOUNT);
            final String password = httpBody.getValue(PASSWORD);
            final String email = httpBody.getValue("email");

            final User user = new User(account, password, email);
            InMemoryUserRepository.save(user);

            return new ResponseEntity(StatusCode.MOVED_TEMPORARILY, REDIRECT_URL);
        }

        return new ResponseEntity(StatusCode.OK, httpHeader.getUrl());
    }
}
