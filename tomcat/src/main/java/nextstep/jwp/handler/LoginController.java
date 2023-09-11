package nextstep.jwp.handler;

import java.util.UUID;
import nextstep.jwp.exception.UnAuthorizationException;
import nextstep.jwp.exception.UnRegisteredUserException;
import nextstep.jwp.model.User;
import nextstep.jwp.service.AuthService;
import org.apache.catalina.exception.NoSuchBodyValueException;
import org.apache.catalina.servlet.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.catalina.util.ResourceFileReader;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpStatus;
import org.apache.coyote.http.SupportFile;
import org.apache.coyote.http.vo.Cookie;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.HttpResponse;
import org.apache.coyote.http.vo.Url;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String ACCOUNT_QUERY_KEY = "account";
    private static final String PASSWORD_QUERY_KEY = "password";

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        if (AuthenticationUser(request)) {
            response.setStatus(HttpStatus.REDIRECT)
                    .setHeader(HttpHeader.LOCATION, "/index.html");
            return;
        }

        response.setStatus(HttpStatus.OK)
                .setHeader(HttpHeader.CONTENT_TYPE, SupportFile.HTML.getContentType())
                .setBody(ResourceFileReader.readFile("/login.html"));
    }

    private boolean AuthenticationUser(final HttpRequest request) {
        return request.hasCookie("JSESSIONID") && SessionManager.findSession(request.getCookie("JSESSIONID")) != null;
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        try {
            final String account = request.getBodyValueOf(ACCOUNT_QUERY_KEY);
            final String password = request.getBodyValueOf(PASSWORD_QUERY_KEY);
            final User user = authService.login(account, password);
            log.info("User ={}", user);
            response.setStatus(HttpStatus.REDIRECT)
                    .setHeader(HttpHeader.LOCATION, "/index.html");

            if (!request.hasCookie("JSESSIONID")) {
                response.setCookie(createJsessionIdCookie(user));
            }

        } catch (UnAuthorizationException | UnRegisteredUserException e) {
            final String body = ResourceFileReader.readFile("/401.html");
            response.setStatus(HttpStatus.UNAUTHORIZED)
                    .setHeader(HttpHeader.CONTENT_TYPE, SupportFile.HTML.getContentType())
                    .setBody(body);

        } catch (NoSuchBodyValueException e) {
            final String body = ResourceFileReader.readFile("/400.html");
            response.setStatus(HttpStatus.BAD_REQUEST)
                    .setHeader(HttpHeader.CONTENT_TYPE, SupportFile.HTML.getContentType())
                    .setBody(body);
        }
    }

    private Cookie createJsessionIdCookie(final User user) {
        final Cookie cookie = Cookie.emptyCookie();
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user);
        SessionManager.add(session.getId(), session);
        cookie.put("JSESSIONID", session.getId());
        return cookie;
    }

    @Override
    public boolean isSupported(final HttpRequest request) {
        return request.isUrl(Url.from("/login"));
    }
}
