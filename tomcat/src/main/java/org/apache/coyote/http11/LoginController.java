package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.cookie.HttpCookie;
import org.apache.session.Session;
import org.apache.session.SessionManager;

import java.io.IOException;
import java.util.Map;

public class LoginController extends AbstractController {

    private final StaticResourceHandler staticResourceHandler;

    public LoginController(StaticResourceHandler staticResourceHandler) {
        this.staticResourceHandler = staticResourceHandler;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (isLogin(request.getHeader("Cookie"))) {
            response.redirectTo("/index.html");
            return;
        }

        staticResourceHandler.serve(request.getPathUri(), response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        User user = login(request);

        if (user != null) {
            String jsessionId = storeSession(request.getHeader("Cookie"), user);
            response.addCookie("JSESSIONID", jsessionId)
                    .redirectTo("/index.html");
            return;
        }

        response.status(HttpStatus.UNAUTHORIZED);
        staticResourceHandler.serve("/401.html", response);
    }

    private User login(HttpRequest request) {
        Map<String, String> queryParams = request.parseQueryParams(request.getRequestBody());

        if (queryParams.get("account") == null || queryParams.get("password") == null) {
            return null;
        }

        return InMemoryUserRepository.findByAccount(queryParams.get("account"))
                .filter(user -> user.checkPassword(queryParams.get("password")))
                .orElse(null);
    }

    private String storeSession(String cookie, User user) {
        String jsessionId = HttpCookie.getJsessionId(cookie);
        SessionManager sessionManager = SessionManager.getInstance();

        Session oldSession = sessionManager.findSession(jsessionId);
        if (oldSession != null) {
            oldSession.invalidate();
            sessionManager.remove(oldSession.getJsessionId());
        }

        Session newSession = new Session();
        sessionManager.add(newSession);
        newSession.setAttribute("user", user);

        return newSession.getJsessionId();
    }

    private boolean isLogin(String cookie) {
        String jsessionId = HttpCookie.getJsessionId(cookie);

        if (jsessionId == null || jsessionId.isBlank()) {
            return false;
        }

        Session session = SessionManager.getInstance().findSession(jsessionId);
        return session != null && session.getAttribute("user") != null;
    }
}
