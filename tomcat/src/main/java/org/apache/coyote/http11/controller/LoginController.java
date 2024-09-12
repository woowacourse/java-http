package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.RequestCookie;
import org.apache.coyote.http11.response.Response;

public class LoginController extends AbstractController {

    private static final SessionManager SESSION_MANAGER = new SessionManager();

    @Override
    protected void doGet(Request request, Response response) throws Exception {
        response.addFileBody("/login.html");
        Optional<RequestCookie> loginCookie = request.getLoginCookie();
        if (loginCookie.isPresent()) {
            RequestCookie cookie = loginCookie.get();
            boolean isLogin = SESSION_MANAGER.contains(cookie.getValue());
            if (isLogin) {
                response.sendRedirection("/index.html");
            }
        }
    }

    @Override
    protected void doPost(Request request, Response response) throws Exception {
        Map<String, String> userInfo = request.parseBody();
        String account = userInfo.get("account");
        String password = userInfo.get("password");

        Optional<User> rawUser = InMemoryUserRepository.findByAccount(account);
        if (rawUser.isEmpty() || !rawUser.get().checkPassword(password)) {
            response.unauthorized();
            response.addFileBody("/401.html");
            return;
        }
        User user = rawUser.get();
        log.info("user: {}", user);
        String newSessionId = SESSION_MANAGER.create("user", user);
        response.addLoginCookie(newSessionId);
        response.sendRedirection("/index.html");
    }
}
