package org.apache.coyote.adapter;

import java.util.Map;
import java.util.UUID;
import org.apache.coyote.handler.LoginHandler;
import org.apache.coyote.http11.HttpCookie;
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
    private static final String JSESSIONID_KEY = "JSESSIONID=";

    @Override
    public Resource adapt(Request request) {
        LoginHandler loginHandler = new LoginHandler();
        if (request.isSameHttpMethod(HttpMethod.GET)) {
            return ViewResource.of(LOGIN_PATH, HttpStatus.OK, HttpCookie.from(""));
        }
        if (request.isSameHttpMethod(HttpMethod.POST)) {
            Map<String, String> body = request.getBody();
            String account = body.get(ACCOUNT);
            String password = body.get(PASSWORD);
            if (loginHandler.login(account, password)) {
                UUID uuid = UUID.randomUUID();
                return ViewResource.of(INDEX_PATH, HttpStatus.FOUND, HttpCookie.from(JSESSIONID_KEY + uuid));
            }
            return ViewResource.of(UNAUTHORIZED_PATH, HttpStatus.UNAUTHORIZED, HttpCookie.from(""));
        }
        throw new IllegalArgumentException("잘못된 HTTP METHOD 요청입니다.");

    }
}
