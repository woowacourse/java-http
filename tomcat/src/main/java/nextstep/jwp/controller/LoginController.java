package nextstep.jwp.controller;

import static nextstep.jwp.utils.Constant.SESSION_ID;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.handler.DashboardException;
import nextstep.jwp.model.User;
import nextstep.jwp.utils.FileFinder;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.FormData;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doPost(HttpRequest request, HttpResponse response)  {
        FormData formData = new FormData(request.getBody());
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(formData.getValue("account"));
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.checkPassword(formData.getValue("password"))) {
                log.info(user.toString());
                addCookieAndSession(request, response, user);
                response.toRedirect("/index.html");
                return;
            }
        }

        throw new DashboardException(HttpStatus.UNAUTHORIZED.code);
    }

    private void addCookieAndSession(HttpRequest request, HttpResponse response, User user) {
        String uuid = UUID.randomUUID().toString();
        SessionManager sessionManager = request.getSessionManager();
        Session session = sessionManager.createSession(uuid);
        session.setAttribute("user", user);
        response.getCookie().put(SESSION_ID, uuid);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (isLogin(request)) {
            response.toRedirect("/index.html");
            return;
        }

        response.setBody(FileFinder.getFileContent("/login.html"));
        response.setHttpStatus(HttpStatus.OK);
    }

    private boolean isLogin(HttpRequest request) {
        Map<String, String> cookie = request.getCookie();
        if (cookie.get(SESSION_ID) != null) {
            String sessionId = cookie.get(SESSION_ID);
            SessionManager sessionManager = request.getSessionManager();
            Session session = sessionManager.findSession(sessionId);
            if (session.getAttribute("user") != null) {
                return true;
            }
        }
        return false;
    }
}
