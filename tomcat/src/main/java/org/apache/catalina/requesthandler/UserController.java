package org.apache.catalina.requesthandler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private static final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String path = request.getPath();
        
        if ("/register".equals(path)) {
            handleRegister(request, response);
        } else if ("/login".equals(path)) {
            handleLogin(request, response);
        }
    }

    private void handleRegister(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> requestBody = request.getBody();
        final String account = requestBody.getOrDefault("account", "");
        final String password = requestBody.getOrDefault("password", "");
        final String email = requestBody.getOrDefault("email", "");
        
        if (account.isBlank() || password.isBlank() || email.isBlank()) {
            response.sendRedirect(ResponseStatus.FOUND, "/401.html");
            return;
        }
        
        final var user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        
        final var session = sessionManager.createSession();
        response.setSession(session);
        response.sendRedirect(ResponseStatus.FOUND, "/index.html");
    }

    private void handleLogin(HttpRequest request, HttpResponse response) throws Exception {
        final Map<String, String> requestBody = request.getBody();
        final String account = requestBody.getOrDefault("account", "");
        final String password = requestBody.getOrDefault("password", "");
        
        if (account.isBlank() || password.isBlank()) {
            response.sendRedirect(ResponseStatus.FOUND, "/index.html");
            return;
        }
        
        final User user = InMemoryUserRepository.findByAccount(account)
             .orElseThrow(UnauthorizedException::new);
         if (!user.checkPassword(password)) {
             throw new UnauthorizedException();
         }
         log.info("회원 조회 성공 : {}", user);
        
        final var session = sessionManager.createSession();
        response.setSession(session);
        response.sendRedirect(ResponseStatus.FOUND, "/index.html");
    }
}