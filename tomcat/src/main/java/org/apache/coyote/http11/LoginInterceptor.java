package org.apache.coyote.http11;

import java.io.IOException;
import nextstep.jwp.LoginFailureException;
import nextstep.jwp.LoginService;
import org.apache.coyote.http11.request.HttpRequest;

public class LoginInterceptor implements Interceptor {

    private final LoginService loginService;

    public LoginInterceptor() {
        this.loginService = new LoginService();
    }

    @Override
    public boolean handle(HttpRequest httpRequest) throws IOException {
        if (httpRequest.isExistQueryString()) {
            final String account = httpRequest.getQueryStringValue("account");
            final String password = httpRequest.getQueryStringValue("password");

            try {
                loginService.validateAccount(account, password);
            } catch (LoginFailureException exception) {
                return false;
            }
        }
        return true;
    }
}
