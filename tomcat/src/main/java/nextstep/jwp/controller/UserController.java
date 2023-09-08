package nextstep.jwp.controller;

import nextstep.jwp.controller.dto.Response;
import nextstep.jwp.service.UserService;
import org.apache.catalina.servlet.adapter.AbstractController;
import org.apache.coyote.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpStatus;

import java.util.NoSuchElementException;
import java.util.UUID;

public class UserController extends AbstractController {
    private final UserService userService = new UserService();


    @Override
    protected ResponseEntity doPost(final HttpRequest request) {
        try {
            if (request.getPath().equals("/login")) {
                final String account = validateValue(request, "account");
                final String password = validateValue(request, "password");
                final UUID newSessionId = userService.login(account, password);
                final Response response = new Response(HttpStatus.FOUND);
                response.setCookie(newSessionId);
                return ResponseEntity.found(response, "index.html");
            }
            if (request.getPath().equals("/register")) {
                final String account = validateValue(request, "account");
                final String password = validateValue(request, "password");
                final String email = validateValue(request, "email");
                final UUID newSessionId = userService.register(account, password, email);
                final Response response = new Response(HttpStatus.FOUND);
                response.setCookie(newSessionId);
                return ResponseEntity.found(response, "index.html");
            }
            return ResponseEntity.found(new Response(HttpStatus.FOUND), "401.html");
        } catch (NoSuchElementException e) {
            return ResponseEntity.found(new Response(HttpStatus.FOUND), "401.html");
        }
    }

    @Override
    protected ResponseEntity doGet(final HttpRequest request) {
        if (request.getPath().equals("/login")) {
            return ResponseEntity.forward(HttpStatus.FOUND, "/login.html");
        }
        if (request.getPath().equals("/register")) {
            return ResponseEntity.forward(HttpStatus.FOUND, "/register.html");
        }
        return ResponseEntity.found(new Response(HttpStatus.FOUND),"/404.html");
    }

    private String validateValue(final HttpRequest request, final String key) {
        final String value = request.getBodyBy(key);
        if (value != null) {
            return value;
        }
        throw new NoSuchElementException();
    }
}
