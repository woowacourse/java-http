package org.apache.coyote.adapter;

import java.util.Map;
import org.apache.coyote.handler.LoginHandler;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.view.Resource;
import org.apache.coyote.view.ViewResource;

public class LoginAdapter implements Adapter {

    private static final String INDEX_PATH = "/index.html";
    private static final String LOGIN_PATH = "/login.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    @Override
    public Resource adapt(Request request) {
        LoginHandler loginHandler = new LoginHandler();
        if (request.isSameHttpMethod(HttpMethod.GET)) {
            return ViewResource.of(LOGIN_PATH, HttpStatus.OK);
        }
        if (request.isSameHttpMethod(HttpMethod.POST)) {
            Map<String, String> body = request.getBody();
            String account = body.get(ACCOUNT);
            String password = body.get(PASSWORD);
            if (loginHandler.login(account, password)) {
                return ViewResource.of(INDEX_PATH, HttpStatus.SUCCESS);
            }
            return ViewResource.of(UNAUTHORIZED_PATH, HttpStatus.UNAUTHORIZED);
        }
        throw new IllegalArgumentException("잘못된 HTTP METHOD 요청입니다.");

    }
}
