package nextstep.jwp.controller;

import static org.apache.coyote.http11.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.HttpStatusCode.FOUND;
import static org.apache.coyote.http11.HttpStatusCode.OK;
import static util.FileLoader.loadFile;

import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestBody;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.exception.badrequest.NotExistHeaderException;
import org.apache.coyote.http11.exception.unauthorized.InvalidSessionException;
import org.apache.coyote.http11.exception.unauthorized.LoginFailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final SessionManager sessionManager;

    public LoginController(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (isLoggedIn(request, sessionManager)) {
            response.statusCode(FOUND)
                    .redirect("/index.html");
            return;
        }
        final String responseBody = loadFile("/login.html");
        response.statusCode(OK)
                .addHeader(CONTENT_TYPE, ContentType.of(request.getFileExtension()).getValue())
                .addHeader(CONTENT_LENGTH, responseBody.getBytes().length)
                .responseBody(responseBody);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final HttpRequestBody httpRequestBody = request.getHttpRequestBody();
        final User user = login(httpRequestBody);
        final UUID id = UUID.randomUUID();
        final Session session = new Session(id.toString());
        session.setAttribute("user", user);
        sessionManager.add(session);
        final HttpCookie httpCookie = HttpCookie.ofJSessionId(session.getId());
        response.statusCode(FOUND)
                .addCookie(httpCookie)
                .redirect("/index.html");
    }

    private boolean isLoggedIn(final HttpRequest httpRequest, final SessionManager sessionManager) {
        try {
            final HttpCookie httpCookie = httpRequest.getHttpCookie();
            sessionManager.findSession(httpCookie.getCookieValue("JSESSIONID"));
            return true;
        } catch (NotExistHeaderException | InvalidSessionException exception) {
            return false;
        }
    }

    private User login(final HttpRequestBody httpRequestBody) {
        final String account = httpRequestBody.getBodyValue("account");
        final String password = httpRequestBody.getBodyValue("password");
        final User user = findRegisteredUser(account);
        if (!user.checkPassword(password)) {
            throw new LoginFailException();
        }
        return user;
    }

    private User findRegisteredUser(final String account) {
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(LoginFailException::new);
        log.info("already register account : {}", account);
        return user;
    }
}
