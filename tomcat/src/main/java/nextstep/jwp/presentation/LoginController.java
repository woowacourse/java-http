package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.FileReader;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final SessionManager sessionManager = new SessionManager();

    @Override
    public HttpResponse process(final HttpRequest httpRequest) {
        if (httpRequest.isGet()) {
            return checkSession(httpRequest);
        }
        try {
            final String account = httpRequest.getHttpBody("account");
            final String password = httpRequest.getHttpBody("password");
            final User user = checkUser(account, password);
            final HttpResponse httpResponse = HttpResponse.found("/index.html", FileReader.read("/index.html"));
            final String jSessionId = HttpCookie.createJSessionId();
            httpResponse.setCookie("JSESSIONID", jSessionId);
            createAndSaveSession(user, jSessionId);
            return httpResponse;
        } catch (UncheckedServletException e) {
            return HttpResponse.unauthorized("/401.html", FileReader.read("/401.html"));
        }
    }

    private HttpResponse checkSession(final HttpRequest httpRequest) {
        if (httpRequest.containsSession()) {
            String session1 = httpRequest.getSession();
            final Session session = sessionManager.findSession(session1);
            final User user = (User) session.getAttribute("user");
            log.info("session user: {}", user.toString());
            return HttpResponse.found("/index.html", FileReader.read("/index.html"));
        }
        return HttpResponse.ok("/login.html", FileReader.read("/login.html"));
    }

    private void createAndSaveSession(final User user, final String jSessionId) {
        final Session session = new Session(jSessionId);
        session.setAttribute("user", user);
        sessionManager.add(session);
    }

    private User checkUser(final String account, final String password) {
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));
        if (!user.checkPassword(password)) {
            throw new UncheckedServletException("비밀번호가 일치하지 않습니다.");
        }
        log.info(user.toString());
        return user;
    }
}
