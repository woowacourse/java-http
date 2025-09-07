package org.apache.catalina;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

public class LoginHandler {

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
        if (sessionId == null) {
            return new HttpResponse(HttpStatusCode.OK, ContentType.HTML, "/login.html");
        }

        Session session = SessionManager.getInstance().findSession(sessionId);
        if (session != null && session.getUser() != null) {
            HttpResponse response = new HttpResponse(HttpStatusCode.FOUND, ContentType.HTML, "/index.html");
            response.setLocation("/index.html");
            return response;
        }

        return new HttpResponse(HttpStatusCode.UNAUTHORIZED, ContentType.HTML, "/401.html");
    }

    private HttpResponse login(HttpRequest request) {
        Map<String, String> requestBody = request.parseQueryStringForm(request.getBody());
        String account = requestBody.get("account");
        String password = requestBody.get("password");
        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("로그인 성공! 아이디: {}", user.get().getAccount());
            HttpResponse response = new HttpResponse(HttpStatusCode.FOUND, ContentType.HTML, "/index.html");
            String sessionId = setUserSession(user.get());
            response.setLocation("/index.html");
            response.addCookie("JSESSIONID", sessionId);
            return response;
        }

        return new HttpResponse(HttpStatusCode.UNAUTHORIZED, ContentType.HTML, "/401.html");
    }

    private String setUserSession(User user) {
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user);
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.add(session);
        return session.getId();
    }
}
