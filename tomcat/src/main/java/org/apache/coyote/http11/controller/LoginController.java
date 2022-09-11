package org.apache.coyote.http11.controller;

import static nextstep.jwp.exception.ExceptionType.SERVER_EXCEPTION;

import com.sun.jdi.InternalException;
import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.jwp.model.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController implements Handler {

    private static final LoginController INSTANCE = new LoginController();
    private static final String LOGIN_HTML_URL = "/login.html";

    private LoginController() {
    }

    @Override
    public void handle(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        try {
            if (isRequestLoginPage(httpRequest)) {
                httpResponse.setOkResponse(LOGIN_HTML_URL);
                return;
            }
            UserService.getInstance().login(httpRequest, httpResponse);

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            throw new InternalException(SERVER_EXCEPTION.getMessage());
        }
    }

    private boolean isRequestLoginPage(HttpRequest httpRequest) {
        return httpRequest.getParams().isEmpty() && !httpRequest.hasCookie();
    }

    public static LoginController getINSTANCE() {
        return INSTANCE;
    }
}
