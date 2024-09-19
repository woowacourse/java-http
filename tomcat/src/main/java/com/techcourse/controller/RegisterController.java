package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Status;
import org.apache.coyote.http11.controller.AbstractController;

public class RegisterController extends AbstractController {

    private final SessionManager sessionManager;

    public RegisterController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String sessionId = request.getCookie(HttpResponse.SESSION_ID_NAME);
        if (sessionManager.hasSession(sessionId)) {
            response.generateResponse("/index.html", Status.FOUND);
            return;
        }
        response.generateResponse("static" + request.getPath(), Status.OK);
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Optional<Map<String, String>> parsed = request.parseQueryString();
        Map<String, String> queryPairs = parsed.orElseThrow(
                () -> new NoSuchElementException("invalid query string")
        );
        register(queryPairs);
        response.generateResponse("/index.html", Status.FOUND);
    }

    private void register(Map<String, String> parsed) {
        validateRegisterKeys(parsed);
        validateRegisteredUser(parsed);
        User newbie = new User(
                parsed.get("account"),
                parsed.get("password"),
                parsed.get("email")
        );
        InMemoryUserRepository.save(newbie);
    }

    private void validateRegisterKeys(Map<String, String> parsed) {
        Set<String> registerKeys = Set.of("account", "password", "email");
        boolean allKeysPresent = registerKeys.stream()
                .allMatch(parsed::containsKey);
        if (!allKeysPresent) {
            throw new NoSuchElementException("invalid query string");
        }
    }

    private void validateRegisteredUser(Map<String, String> parsed) {
        String inputAccount = parsed.get("account");
        if (InMemoryUserRepository.existsByAccount(inputAccount)) {
            throw new IllegalArgumentException("account already exists");
        }
    }
}
