package nextstep.jwp.controller;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class LoginController extends Controller {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/login.html";

    private static final SessionManager sessionManager = new SessionManager();

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        if (isLogin(request)) {
            redirect(response, INDEX_PAGE);
            return;
        }
        redirect(response, LOGIN_PAGE);
    }

    private boolean isLogin(HttpRequest request) {
        if (request.hasCookie()) {
            String sessionId = request.getCookieValue(JSESSIONID);
            Session session = sessionManager.findSession(sessionId);
            return InMemoryUserRepository.findByAccount(session.getAttribute("user"))
                    .isPresent();
        }
        return false;
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> bodyForm = request.body()
                .toApplicationForm();
        if (isUser(bodyForm)) {
            saveInSession(response, bodyForm);
            redirect(response, INDEX_PAGE);
            return;
        }
        redirect(response, "/401.html");
    }

    private void saveInSession(HttpResponse response, Map<String, String> bodyForm) {
        Session session = new Session();
        session.setAttribute("user", bodyForm.get("account"));
        sessionManager.add(session);
        response.setHeader("Set-Cookie", JSESSIONID + "=" + session.getId());
    }

    private boolean isUser(Map<String, String> bodyForm) {
        if (!bodyForm.containsKey("account") || !bodyForm.containsKey("password")) {
            return false;
        }
        Optional<User> userOptional = InMemoryUserRepository.findByAccount(bodyForm.get("account"));
        return userOptional.isPresent() &&
                userOptional.get()
                        .checkPassword(bodyForm.get("password"));
    }

    private void redirect(HttpResponse response, String value) {
        response.setStatus(HttpStatus.FOUND);
        response.setHeader("Location", value);
    }
}
