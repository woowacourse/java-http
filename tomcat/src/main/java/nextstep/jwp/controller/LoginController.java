package nextstep.jwp.controller;

import java.util.Optional;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String JSESSIONID = "JSESSIONID";

    private static final LoginController INSTANCE = new LoginController();
    private static final SessionManager sessionManager = new SessionManager();

    public static LoginController getInstance() {
        return INSTANCE;
    }

    private LoginController() {
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (request.hasSession()) {
            String sessionId = request.getCookieValue(JSESSIONID);
            Session session = sessionManager.findSession(sessionId);
            User user = (User) session.getAttribute("user");

            HttpResponse.redirect(response, INDEX_PAGE);
            response.addCookie(session.getId());
            return;
        }

        ResourceController.returnResource(LOGIN_PAGE, response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Optional<User> findUser = InMemoryUserRepository.findByAccount(request.getBodyValue(ACCOUNT));
        if (findUser.isEmpty() || !findUser.get().checkPassword(request.getBodyValue(PASSWORD))) {
            HttpResponse.redirect(response, UNAUTHORIZED_PAGE);
            return;
        }

        Session session = new Session();
        session.setAttribute("user", findUser.get());
        sessionManager.add(session);

        HttpResponse.redirect(response, INDEX_PAGE);
        response.addCookie(session.getId());
    }
}
