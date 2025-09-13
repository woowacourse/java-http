package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.StaticResource;
import org.apache.coyote.http11.StaticResourceProvider;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(Http11Request request, Http11Response response) throws Exception {
        final String sessionId = request.getCookie("JSESSIONID");
        if (isLoggedIn(sessionId)) {
            response.setHttpStatus(HttpStatus.FOUND);
            response.addHeader("Location", "/index.html");
            return;
        }

        final StaticResource staticResource = StaticResourceProvider.getStaticResource("/login.html");
        response.setStaticResource(staticResource);
        response.setHttpStatus(HttpStatus.OK);
    }

    @Override
    protected void doPost(Http11Request request, Http11Response response) throws Exception {
        final String account = request.getBodyParam("account");
        final String password = request.getBodyParam("password");

        if (isLoginSuccess(account, password)) {
            final Session session = Session.createNewSession();
            session.setAttribute("account", account);
            SessionManager.add(session);
            response.addCookie("JSESSIONID", session.getId());

            response.setHttpStatus(HttpStatus.FOUND);
            response.addHeader("Location", "/index.html");

            return;
        }

        final StaticResource staticResource = StaticResourceProvider.getStaticResource("/401.html");
        response.setStaticResource(staticResource);
        response.setHttpStatus(HttpStatus.UNAUTHORIZED);
    }

    private boolean isLoggedIn(final String sessionId) {
        final Session session = SessionManager.findSession(sessionId);
        return session != null;
    }

    private boolean isLoginSuccess(final String account, final String password) {
        log.debug("account : {} password : {}", account, password);
        Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
        return userOptional
                .map(user -> user.checkPassword(password))
                .orElse(false);
    }
}
