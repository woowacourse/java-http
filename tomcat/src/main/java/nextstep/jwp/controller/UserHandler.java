package nextstep.jwp.controller;

import nextstep.jwp.controller.dto.Response;
import nextstep.jwp.service.UserService;
import org.apache.catalina.servlet.adapter.AbstractHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import java.util.NoSuchElementException;
import java.util.UUID;

public class UserHandler extends AbstractHandler {
    private final UserService userService = new UserService();


    @Override
    protected void doPost(final HttpRequest request ,final HttpResponse httpResponse) {
        try {
            if (request.getPath().equals("/login")) {
                final String account = validateValue(request, "account");
                final String password = validateValue(request, "password");
                final UUID newSessionId = userService.login(account, password);
                final Response response = new Response(HttpStatus.FOUND);
                response.setCookie(newSessionId);
                httpResponse.setHeader(request.getHttpVersion(), response.getHttpStatus(), response.getResponseHeader());
                httpResponse.setRedirect("index.html");
                return;
            }
            if (request.getPath().equals("/register")) {
                final String account = validateValue(request, "account");
                final String password = validateValue(request, "password");
                final String email = validateValue(request, "email");
                final UUID newSessionId = userService.register(account, password, email);
                final Response response = new Response(HttpStatus.FOUND);
                response.setCookie(newSessionId);
                httpResponse.setHeader(request.getHttpVersion(), response.getHttpStatus(), response.getResponseHeader());
                httpResponse.setRedirect("index.html");
                return;
            }
            httpResponse.setHttpStatus(request.getHttpVersion(), HttpStatus.FOUND);
            httpResponse.setRedirect("401.html");
        } catch (NoSuchElementException e) {
            httpResponse.setHttpStatus(request.getHttpVersion(), HttpStatus.FOUND);
            httpResponse.setRedirect("404.html");
        }
    }

    private String validateValue(final HttpRequest request, final String key) {
        final String value = request.getBodyBy(key);
        if (value != null) {
            return value;
        }
        throw new NoSuchElementException();
    }
}
