package org.apache.coyote.adapter;

import java.util.Map;
import org.apache.coyote.handler.RegisterHandler;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.view.Resource;
import org.apache.coyote.view.ViewResource;

public class RegisterAdapter implements Adapter {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    @Override
    public Resource adapt(Request request) {
        if (request.isSameHttpMethod(HttpMethod.POST)) {
            RegisterHandler registerHandler = new RegisterHandler();
            Map<String, String> requestBody = request.getBody();
            String account = requestBody.get(ACCOUNT);
            String password = requestBody.get(PASSWORD);
            String email = requestBody.get(EMAIL);
            registerHandler.register(account, password, email);
            return ViewResource.of("/index.html", HttpStatus.FOUND, HttpCookie.from(""));
        }
        if (request.isSameHttpMethod(HttpMethod.GET)) {
            return ViewResource.of("/register.html", HttpStatus.OK, HttpCookie.from(""));
        }
        throw new IllegalArgumentException("잘못된 HTTP METHOD 요청입니다.");
    }
}
