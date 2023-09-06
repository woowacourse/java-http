package org.apache.coyote.adapter;

import org.apache.coyote.handler.LoginHandler;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;

public class LoginAdapter implements Adapter {

    private static final String DEFAULT_RESOURCE_PATH = "/static";
    private static final String INDEX_PATH = "/index.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    @Override
    public Response doHandle(Request request) {
        LoginHandler loginHandler = new LoginHandler();
        if (!request.hasQueryString()) {
            throw new IllegalArgumentException("올바른 요청이 아닙니다.");
        }
        loginHandler.login(request.getQueryStringValue(ACCOUNT), request.getQueryStringValue(PASSWORD));

        return new ResourceAdapter().doHandle(request);
    }
}
