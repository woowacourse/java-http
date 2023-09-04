package nextstep.jwp.controller;

import nextstep.jwp.controller.dto.Response;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.response.HttpStatus;

import java.util.Map;
import java.util.UUID;

public class UserController {
    private final UserService userService = new UserService();

    public Response login(final Map<String, String> requestBody) {
        final String account;
        final String password;
        try {
            account = requestBody.get("account");
            password = requestBody.get("password");
        } catch (Exception e) {
            throw new IllegalArgumentException(HttpStatus.BAD_REQUEST.name());
        }
        final UUID session = userService.login(account, password);
        final Response response = new Response(HttpStatus.OK);
        response.setCookie(session);
        return response;
    }

    public Response register(final Map<String, String> requestBody) {
        final String account;
        final String password;
        final String email;
        try {
            account = requestBody.get("account");
            password = requestBody.get("password");
            email = requestBody.get("email");
        }catch (Exception e) {
            throw new IllegalArgumentException(HttpStatus.BAD_REQUEST.toString());
        }
        final UUID session = userService.register(account, password, email);
        final Response response = new Response(HttpStatus.CREATED);
        response.setCookie(session);
        return response;
    }
}
