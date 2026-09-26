package com.techcourse.controller;

import com.techcourse.model.User;
import com.techcourse.service.UserSessionService;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

import java.util.Optional;

public final class SessionController extends AbstractController {

    private final UserSessionService userSessions = new UserSessionService();

    public SessionController() {
        super(HttpMethod.GET);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        Optional<User> loginUser = userSessions.findUser(request);
        String responseBody = loginUser
                .map(user -> "{\"loggedIn\":true,\"account\":\""
                        + escapeJson(user.getAccount()) + "\"}")
                .orElse("{\"loggedIn\":false}");

        response.setStatus(HttpStatus.OK);
        response.setContentType("application/json");
        response.setBody(responseBody);
    }

    private String escapeJson(String value) {
        StringBuilder escaped = new StringBuilder();
        for (char character : value.toCharArray()) {
            switch (character) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (character < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
                }
            }
        }
        return escaped.toString();
    }
}
