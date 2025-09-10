package org.apache.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import java.util.UUID;
import org.apache.exception.InvalidRequestException;
import org.apache.http.Cookie;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;
import org.apache.http.value.HttpMethod;
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
        validateRequestBody(request);

        String account = request.getBody("account");
        String email = request.getBody("email");
        String password = request.getBody("password");
        validateAlreadyAccountExistence(account);

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        String sessionId = makeSession(user);
        response.setRedirection("/index.html");
        response.setCookie(Cookie.makeSessionCookie(sessionId));
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

    private void validateRequestBody(HttpRequest request) {
        String account = request.getBody("account");
        String email = request.getBody("email");
        String password = request.getBody("password");
        if (account == null || account.isEmpty() &&
                email == null || email.isEmpty() &&
                password == null || password.isEmpty()) {
            throw new InvalidRequestException("잘못된 회원가입 형식입니다.");
        }
    }
}
