package org.apache.coyote.http11;

import org.apache.coyote.Interceptor;
import org.apache.coyote.http11.request.HttpRequest;

public class LoginInterceptor implements Interceptor {

    private final LoginService loginService;

    public LoginInterceptor() {
        this.loginService = new LoginService();
    }

    @Override
    public void handle(HttpRequest httpRequest) {
        if(httpRequest.isExistQueryString()) {
            final String account = httpRequest.getQueryStringValue("account");
            final String password = httpRequest.getQueryStringValue("password");
            loginService.validateAccount(account, password);
        }
    }
}
