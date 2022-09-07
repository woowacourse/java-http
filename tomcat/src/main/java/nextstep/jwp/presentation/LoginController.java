package nextstep.jwp.presentation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
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
        response.setStatus(HttpStatus.OK);
        response.setBody(body);
        response.flush();
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        Map<String, String> values = getAccountAndPassword(request);
        String account = values.get("account");
        String password = values.get("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("not found account"));

        if (!user.checkPassword(password)) {
            redirect(response, "/401.html");
            return;
        }

        log.info(user.toString());
        response(response, user);
    }

    private void redirect(final HttpResponse response, final String redirectUrl) throws IOException {
        response.setStatus(HttpStatus.FOUND);
        response.redirect(redirectUrl);
        response.flush();
    }

    private Map<String, String> getAccountAndPassword(final HttpRequest httpRequest) {
        String body = httpRequest.getBody();
        String[] split = body.split("&");
        Map<String, String> values = new HashMap<>();
        for (String value : split) {
            String[] keyAndValue = value.split("=");
            values.put(keyAndValue[0], keyAndValue[1]);
        }
        return values;
    }

    private void response(final HttpResponse response, final User user) throws IOException {
        String cookie = CookieUtils.ofJSessionId();
        setSession(user, cookie);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.addLocation("/index.html ");
        httpHeaders.addCookie(Session.JSESSIONID + "=" + cookie);

        response.setStatus(HttpStatus.FOUND);
        response.setHeaders(httpHeaders);
        response.flush();
    }

    private void setSession(final User user, final String cookie) {
        Session session = new Session(cookie);
        session.setAttribute("user", user);
        SessionManager.add(session);
    }
}
