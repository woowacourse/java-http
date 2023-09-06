package org.apache.coyote.adapter;

import org.apache.coyote.handler.LoginHandler;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.response.Response;
import org.apache.coyote.view.ViewResolver;
import org.apache.coyote.view.ViewResource;

public class LoginAdapter implements Adapter {

    private static final String INDEX_PATH = "/index.html";
    private static final String LOGIN_PATH = "/login.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    @Override
    public Response doHandle(Request request) {
        LoginHandler loginHandler = new LoginHandler();
        if (!request.hasQueryString()) {
            return new ViewResolver().resolve(request, ViewResource.of(LOGIN_PATH, HttpStatus.OK));
        }
        if (loginHandler.login(request.getQueryStringValue(ACCOUNT), request.getQueryStringValue(PASSWORD))) {
            return new ViewResolver().resolve(request, ViewResource.of(INDEX_PATH, HttpStatus.SUCCESS));
        }
        return new ViewResolver().resolve(request, ViewResource.of(UNAUTHORIZED_PATH, HttpStatus.UNAUTHORIZED));
    }
}
