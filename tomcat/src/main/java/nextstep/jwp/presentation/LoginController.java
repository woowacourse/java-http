package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.startup.session.Session;
import org.apache.catalina.startup.session.SessionManager;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.common.FileReader;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        if (httpRequest.containsSession()) {
            final Session session = SessionManager.findSession(httpRequest.getSession());
            final User user = (User) session.getAttribute("user");
            log.info("session-user: {}", user.toString());
            return HttpResponse.redirect(StatusCode.FOUND, "/index.html");
        }
        return HttpResponse.ok("/login.html", FileReader.read("/login.html"));
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        try {
            final String account = httpRequest.getHttpBody("account");
            final String password = httpRequest.getHttpBody("password");
            final User user = checkUser(account, password);
            final HttpResponse httpResponse = HttpResponse.redirect(StatusCode.FOUND, "/index.html");
            final String jSessionId = HttpCookie.createJSessionId();
            httpResponse.setCookie("JSESSIONID", jSessionId);
            createAndSaveSession(user, jSessionId);
            return httpResponse;
        } catch (RuntimeException e) {
            return HttpResponse.unauthorized();
        }
    }

    private User checkUser(final String account, final String password) {
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        if (!user.checkPassword(password)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        log.info(user.toString());
        return user;
    }

    private void createAndSaveSession(final User user, final String jSessionId) {
        final Session session = new Session(jSessionId);
        session.setAttribute("user", user);
        SessionManager.add(session);
    }
}
