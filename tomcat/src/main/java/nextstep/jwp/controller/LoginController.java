package nextstep.jwp.controller;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBuilder;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class LoginController extends AbstractController {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/login.html";

    private static final SessionManager sessionManager = new SessionManager();

    @Override
    public HttpResponse doGet(HttpRequest request, HttpResponseBuilder responseBuilder) {
        if (isLogin(request)) {
            return responseBuilder.redirect(INDEX_PAGE);
        }
        return responseBuilder.redirect(LOGIN_PAGE);
    }

    @Override
    public HttpResponse doPost(HttpRequest request, HttpResponseBuilder responseBuilder) {
        Map<String, String> bodyForm = request.body()
                .toApplicationForm();
        if (isUser(bodyForm)) {
            saveInSession(responseBuilder, bodyForm);
            return responseBuilder.redirect(INDEX_PAGE);
        }
        return responseBuilder.redirect("/401.html");
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

    private void saveInSession(HttpResponseBuilder responseBuilder, Map<String, String> bodyForm) {
        Session session = new Session();
        session.setAttribute("user", bodyForm.get("account"));
        sessionManager.add(session);
        responseBuilder.setHeader("Set-Cookie", JSESSIONID + "=" + session.getId());
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
}
