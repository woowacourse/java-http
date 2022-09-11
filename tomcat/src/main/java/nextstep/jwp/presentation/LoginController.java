package nextstep.jwp.presentation;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.model.UserRequest;
import org.apache.coyote.http11.http.HttpCookie;
import org.apache.coyote.http11.http.HttpHeaders;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.http.Session;
import org.apache.coyote.support.AbstractController;
import org.apache.coyote.util.CookieUtils;
import org.apache.coyote.util.FileUtils;
import org.apache.coyote.util.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        HttpHeaders headers = request.getHeaders();
        HttpCookie cookies = headers.getCookies();
        Session session = getSession(cookies);
        if (session != null) {
            redirect(response, "/index.html");
            return;
        }
        write(request, response);
    }

    private Session getSession(final HttpCookie cookies) {
        if (cookies.has(Session.JSESSIONID)) {
            return SessionManager.findSession(cookies.get(Session.JSESSIONID));
        }
        return null;
    }

    private void write(final HttpRequest request, final HttpResponse response) throws IOException {
        String body = FileUtils.readAllBytes(request.getPath().getValue());
        response.setBody(body);
        response.forward(request.getPath());
        response.write();
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        UserRequestHandler requestHandler = new UserRequestHandler();
        Map<String, String> attribute = requestHandler.handle(request);
        String account = attribute.get("account");
        String password = attribute.get("password");
        UserRequest user = new UserRequest(account, password);
        login(user, response);
    }

    private void login(final UserRequest request, final HttpResponse response) throws IOException {
        String account = request.getAccount();
        String password = request.getPassword();
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("not found account"));

        if (!user.checkPassword(password)) {
            redirect(response, "/401.html");
            return;
        }

        log.info(user.toString());
        forward(response, user);
    }

    private void redirect(final HttpResponse response, final String redirectUrl) throws IOException {
        response.setStatus(HttpStatus.FOUND);
        response.redirect(redirectUrl);
        response.write();
    }

    private void forward(final HttpResponse response, final User user) throws IOException {
        String cookie = CookieUtils.ofJSessionId();
        setSession(user, cookie);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.addLocation("/index.html ");
        httpHeaders.addCookie(Session.JSESSIONID + "=" + cookie);

        response.setStatus(HttpStatus.FOUND);
        response.setHeaders(httpHeaders);
        response.write();
    }

    private void setSession(final User user, final String cookie) {
        Session session = new Session(cookie);
        session.setAttribute("user", user);
        SessionManager.add(session);
    }
}
