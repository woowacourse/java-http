package org.apache.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import java.util.UUID;
import org.apache.exception.InvalidRequestException;
import org.apache.http.Cookie;
import org.apache.http.HttpMethod;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusCode;
import org.apache.session.Session;
import org.apache.session.SessionManager;

public class RegisterController implements Controller {

    @Override
    public boolean isProcessableRequest(HttpRequest request) {
        return request.getMethod() == HttpMethod.POST
                && request.getUri().equals("/register");
    }

    @Override
    public void processRequest(HttpRequest request, HttpResponse response) {
        String account = request.getBody("account");
        String email = request.getBody("email");
        String password = request.getBody("password");
        validateAlreadyAccountExistence(account);

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        String sessionId = makeSession(user);
        response.setStatusCode(StatusCode.FOUND);
        response.setHeader("Location", "/index.html");
        response.setCookie(new Cookie("JSESSIONID", sessionId));
    }

    private void validateAlreadyAccountExistence(String account) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            throw new InvalidRequestException("이미 존재하는 유저입니다.");
        }
    }

    private String makeSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        SessionManager.add(new Session(sessionId, user));
        return sessionId;
    }
}
