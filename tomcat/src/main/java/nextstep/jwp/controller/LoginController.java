package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import nextstep.jwp.FileFinder;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doPost(HttpRequest request, HttpResponse response)  {
        Map<String, String> body = Arrays.stream(request.getBody().split("&"))
            .map(it -> it.split("="))
            .collect(Collectors.toMap(
                keyAndValue -> keyAndValue[0],
                keyAndValue -> keyAndValue[1]));
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(body.get("account"));
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.checkPassword(body.get("password"))) {
                log.info(user.toString());
                addCookieAndSession(request, response, user);
                response.toRedirect("/index.html");
                return;
            }
        }

        throw new MyException(HttpStatus.UNAUTHORIZED.code);
    }

    private void addCookieAndSession(HttpRequest request, HttpResponse response, User user) {
        String uuid = UUID.randomUUID().toString();
        SessionManager sessionManager = request.getSessionManager();
        Session session = sessionManager.createSession(uuid);
        session.setAttribute("user", user);
        response.getCookie().put("JSESSIONID", uuid);
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
        if (cookie.get("JSESSIONID") != null) {
            String sessionId = cookie.get("JSESSIONID");
            SessionManager sessionManager = request.getSessionManager();
            Session session = sessionManager.findSession(sessionId);
            if (session.getAttribute("user") != null) {
                return true;
            }
        }
        return false;
    }
}
