package org.apache.coyote.support;

import nextstep.jwp.presentation.LoginController;
import org.apache.coyote.exception.BadRequestException;
import org.apache.coyote.http11.request.model.HttpRequest;
import org.apache.coyote.http11.request.model.HttpRequestUri;

public class Controller {

    private final HttpRequest httpRequest;

    public Controller(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public String execute(final HttpRequestUri httpRequestUri) {
        if (isLogin(httpRequestUri.getValue())) {
            LoginController loginController = new LoginController();
            return loginController.login(httpRequest);
        }
        throw new BadRequestException(httpRequestUri.getValue());
    }

    private boolean isLogin(final String uri) {
        String requestUri = uri.substring(0, uri.indexOf("?"));
        return requestUri.equals("/login");
    }
}
