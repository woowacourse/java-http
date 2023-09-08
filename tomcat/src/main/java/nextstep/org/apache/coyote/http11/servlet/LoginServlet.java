package nextstep.org.apache.coyote.http11.servlet;

import java.util.NoSuchElementException;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidLoginInfoException;
import nextstep.jwp.model.User;
import nextstep.org.apache.coyote.http11.Cookies;
import nextstep.org.apache.coyote.http11.Http11Request;
import nextstep.org.apache.coyote.http11.Http11Response;
import nextstep.org.apache.coyote.http11.Session;
import nextstep.org.apache.coyote.http11.SessionManager;
import nextstep.org.apache.coyote.http11.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServlet extends AbstractServlet {

    private final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    @Override
    protected void doPost(Http11Request request, Http11Response response) {
        Cookies cookies = request.getCookies();
        try {
            Session loginSession = login(
                    request.getParsedBodyValue("account"),
                    request.getParsedBodyValue("password")
            );
            cookies.set("JSESSIONID", loginSession.getId());

            response.setStatus(Status.FOUND)
                    .setHeader("Location", "/index.html")
                    .setCookies(cookies);
        } catch (NoSuchElementException | InvalidLoginInfoException | NullPointerException e) {
            response.setStatus(Status.FOUND)
                    .setHeader("Location", "/401.html")
                    .setCookies(cookies);
        }
    }

    private Session login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();
        if (!user.checkPassword(password)) {
            throw new InvalidLoginInfoException();
        }
        log.info(user.toString());
        return createSession(user);
    }

    private Session createSession(User user) {
        SessionManager sessionManager = new SessionManager();
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute(user.getAccount(), user);
        sessionManager.add(session);
        return session;
    }

    @Override
    protected void doGet(Http11Request request, Http11Response response) throws Exception {
        // Todo: 헤더에 담긴 sessionId 유효성 검증
        Cookies cookies = request.getCookies();
        if (cookies.hasCookie("JSESSIONID")) {
            response.setStatus(Status.FOUND)
                    .setHeader("Location", "/index.html");
            return;
        }
        responseWithBody(request, response);
    }
}
