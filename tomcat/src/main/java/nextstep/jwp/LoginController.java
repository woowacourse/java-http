package nextstep.jwp;

import static org.reflections.Reflections.log;

import java.io.IOException;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.LoginException;
import nextstep.jwp.model.User;
import org.apache.catalina.AbstractController;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.FileExtractor;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;

public class LoginController extends AbstractController {

    private static final SessionManager sessionManager = new SessionManager();

    private static final String JSESSIONID = "JSESSIONID";
    private static final String INDEX = "/index";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String COOKIE = "Cookie";
    private static final String USER = "user";
    private static final String UNAUTHORIZED = "/401";

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws IOException {
        try {
            final String userName = request.getRequestBody().get(ACCOUNT);
            final String password = request.getRequestBody().get(PASSWORD);

            final User user = InMemoryUserRepository.findByAccount(userName)
                    .orElseThrow(LoginException::new);

            if (user.checkPassword(password)) {
                log.info("user : " + user);
                final HttpCookie cookie = HttpCookie.from(request.getRequestHeaders().geHeaderValue(COOKIE));
                checkSession(user, cookie);
                final ResponseBody responseBody = FileExtractor.extractFile(INDEX);
                final HttpResponse httpResponse = HttpResponse.of(HttpStatusCode.FOUND, responseBody);
                httpResponse.addCookie(cookie);
                return httpResponse;
            }
            throw new LoginException();
        } catch (LoginException exception) {
            final ResponseBody responseBody = FileExtractor.extractFile(UNAUTHORIZED);
            return HttpResponse.of(HttpStatusCode.UNAUTHORIZED, responseBody);
        }
    }

    private void checkSession(final User user, final HttpCookie cookie) {
        if (!cookie.contains(JSESSIONID)) {
            final Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute(USER, user);
            sessionManager.add(session);
            cookie.setCookie(JSESSIONID, session.getId());
        }
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        final HttpCookie cookie = HttpCookie.from(request.getRequestHeaders().geHeaderValue(COOKIE));
        final String requestResource = request.getRequestPath().getResource();

        if (cookie.contains(JSESSIONID)) {
            final ResponseBody responseBody = FileExtractor.extractFile(INDEX);
            final HttpResponse httpResponse = HttpResponse.of(HttpStatusCode.FOUND, responseBody);
            httpResponse.addCookie(cookie);
            return httpResponse;
        }
        final ResponseBody responseBody = FileExtractor.extractFile(requestResource);
        return HttpResponse.of(HttpStatusCode.OK, responseBody);
    }
}
