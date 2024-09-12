package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends AbstractController {

    private final SessionManager sessionManager;

    public RegisterController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String sessionId = request.getCookie("JSESSIONID");
        if (sessionManager.hasSession(sessionId)) {
            response.generate302Response("/index.html");
            return;
        }
        String responseBody = response.generateResponseBody("static" + request.getPath());
        response.generate200Response(request.getPath(), responseBody);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Optional<Map<String, String>> parsed = request.parseQueryString();
        Map<String, String> queryPairs = parsed.orElseThrow(
                () -> new NoSuchElementException("invalid query string")
        );
        register(queryPairs);
        response.generate302Response("/index.html");
    }

    private void register(Map<String, String> parsed) {
        validateRegisterKeys(parsed);
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
}
