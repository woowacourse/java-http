package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.dto.LoginRequest;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.QueryStringConverter;
import nextstep.jwp.http.Session;
import nextstep.jwp.http.SessionManager;
import nextstep.jwp.model.User;
import nextstep.jwp.support.View;
import org.apache.catalina.support.Resource;
import org.apache.coyote.HttpHeader;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.support.Request;
import org.apache.coyote.support.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LoginController extends AbstractController {

    private static final String COOKIE_SESSION_KEY = "JSESSIONID";

    private final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final IdGenerator idGenerator;

    public LoginController(final IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public void doGet(final Request request, final Response response) {
        final Resource resource = new Resource(View.LOGIN.getValue());
        response.header(HttpHeader.CONTENT_TYPE, resource.getContentType().getValue())
                .content(resource.read());
    }

    @Override
    public void doPost(final Request request, final Response response) {
        final LoginRequest loginRequest = convert(request.getContent());
        final User user = InMemoryUserRepository.findByAccount(loginRequest.getAccount())
                .orElseThrow(UnauthorizedException::new);
        if (isPasswordNotMatch(user, loginRequest.getPassword())) {
            throw new UnauthorizedException();
        }
        log.debug(user.toString());
        final Session session = makeSession(user);
        final HttpCookie responseCookie = makeCookie(session);
        response.header(HttpHeader.SET_COOKIE, responseCookie.parse())
                .header(HttpHeader.LOCATION, View.INDEX.getValue())
                .httpStatus(HttpStatus.FOUND);
    }

    private LoginRequest convert(final String queryString) {
        final Map<String, String> paramMapping = QueryStringConverter.convert(queryString);
        return LoginRequest.of(paramMapping);
    }

    private boolean isPasswordNotMatch(final User user, final String password) {
        return !user.isSamePassword(password);
    }

    private Session makeSession(final User user) {
        final Session session = new Session(idGenerator.generate());
        session.setAttribute("user", user);
        final SessionManager sessionManager = SessionManager.get();
        sessionManager.add(session);
        return session;
    }

    private HttpCookie makeCookie(final Session session) {
        final HttpCookie responseCookie = HttpCookie.create();
        responseCookie.put(COOKIE_SESSION_KEY, session.getId());
        return responseCookie;
    }
}
