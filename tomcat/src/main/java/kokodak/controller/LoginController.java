package kokodak.controller;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import kokodak.RequestMapper;
import kokodak.http.FormDataParser;
import kokodak.http.HttpCookie;
import kokodak.http.HttpRequest;
import kokodak.http.HttpResponse;
import kokodak.http.Session;
import kokodak.http.SessionManager;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    static {
        RequestMapper.register("/login", new LoginController());
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception {
        final Map<String, String> formData = FormDataParser.parse(httpRequest.getBody());
        final String account = formData.get("account");
        final String password = formData.get("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                                                .orElseThrow(() -> new IllegalArgumentException("User Not Found"));

        if (isValidPassword(user, password)) {
            log.info("user = {}", user);

            final String sessionId = UUID.randomUUID().toString();
            final Session session = new Session(sessionId);
            session.setAttribute("user", user);
            SessionManager.addSession(session);

            httpResponse.redirect("http://localhost:8080/index.html");
            httpResponse.cookie("JSESSIONID=" + sessionId);
        } else {
            httpResponse.redirect("http://localhost:8080/401.html");
        }
    }

    private boolean isValidPassword(final User user, final String password) {
        return user.checkPassword(password);
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception {
        final HttpCookie httpCookie = httpRequest.getHttpCookie();
        final String jSessionId = httpCookie.cookie("JSESSIONID");
        final Session session = SessionManager.findSession(jSessionId);

        if (session == null) {
            doGetLoginPage(httpRequest, httpResponse);
        } else {
            httpResponse.redirect("http://localhost:8080/index.html");
        }
    }

    private void doGetLoginPage(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final String fileName = "static/login.html";
        httpResponse.setBody(fileName, httpRequest.header("Accept"));
    }
}
