package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.dto.LoginRequest;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.*;
import nextstep.jwp.model.User;
import nextstep.jwp.support.View;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.http.HttpHeader;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class LoginController implements Controller {

    private static final String COOKIE_SESSION_KEY = "JSESSIONID";

    private final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final IdGenerator idGenerator;

    public LoginController(final IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public Response execute(final Request request) {
        final LoginRequest loginRequest = convert(request.getContent());
        final Optional<User> wrappedUser = InMemoryUserRepository.findByAccount(loginRequest.getAccount());
        if (wrappedUser.isPresent()) {
            final User user = wrappedUser.get();
            if (user.isSamePassword(loginRequest.getPassword())) {
                log.debug(user.toString());
                final Session session = makeSession(user);
                final Headers headers = makeHeaders(session);
                return makeResponse(headers);
            }
        }
        throw new UnauthorizedException();
    }

    private Session makeSession(final User user) {
        final Session session = new Session(idGenerator.generate());
        session.setAttribute("user", user);
        final SessionManager sessionManager = SessionManager.get();
        sessionManager.add(session);
        return session;
    }

    private Headers makeHeaders(final Session session) {
        final Headers headers = new Headers();
        final HttpCookie responseCookie = HttpCookie.create();
        responseCookie.put(COOKIE_SESSION_KEY, session.getId());
        headers.put(HttpHeader.SET_COOKIE, responseCookie.parse());
        return headers;
    }

    private Response makeResponse(final Headers headers) {
        headers.put(HttpHeader.LOCATION, View.INDEX.getValue());
        return new Response(headers).httpStatus(HttpStatus.FOUND);
    }

    private LoginRequest convert(final String queryString) {
        final Map<String, String> paramMapping = QueryStringConverter.convert(queryString);
        return LoginRequest.of(paramMapping);
    }
}
