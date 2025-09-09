package org.apache.catalina.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
    private static final LoginHandler INSTANCE = new LoginHandler();

    public static LoginHandler getInstance() {
        return INSTANCE;
    }

    public HttpResponse handle(HttpRequest request) {
        if (request.getMethod().equals("GET")) {
            return getLoginPage(request);
        }
        if (request.getMethod().equals("POST")) {
            return login(request);
        }

        return new HttpResponse(HttpStatusCode.NOT_FOUND, ContentType.HTML, "/401.html"); // TODO: 405 처리 필요
    }

    private HttpResponse getLoginPage(HttpRequest request) {
        String sessionId = request.getCookie("JSESSIONID");
        Session session = (sessionId != null)
                ? SessionManager.getInstance().findSession(sessionId)
                : null;

        if (sessionId == null || session == null || session.getUser() == null) {
            return new HttpResponse(HttpStatusCode.OK, ContentType.HTML, "/login.html");
        }

        HttpResponse response = new HttpResponse(HttpStatusCode.FOUND, ContentType.HTML, "/index.html");
        response.setLocation("/index.html");
        return response;
    }

    private HttpResponse login(HttpRequest request) {
        Map<String, String> requestBody = request.parseQueryStringForm(request.getBody());
        String account = requestBody.get("account");
        String password = requestBody.get("password");
        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("로그인 성공! 아이디: {}", user.get().getAccount());
            HttpResponse response = new HttpResponse(HttpStatusCode.FOUND, ContentType.HTML, "/index.html");
            setUserSession(request, user.get());
            response.setLocation("/index.html");
            return response;
        }

        return new HttpResponse(HttpStatusCode.UNAUTHORIZED, ContentType.HTML, "/401.html");
    }

    private void setUserSession(HttpRequest request, User user) {
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = sessionManager.findSession(request.getCookie("JSESSIONID"));
        session.setAttribute("user", user);
    }
}
