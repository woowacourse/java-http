package org.apache.coyote.adapter;

import java.util.Map;
import org.apache.coyote.handler.RegisterHandler;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.view.Resource;
import org.apache.coyote.view.ViewResource;

public class RegisterAdapter implements Adapter {

    @Override
    public Resource adapt(Request request) {
        if (request.getHttpMethod() == HttpMethod.POST) {
            RegisterHandler registerHandler = new RegisterHandler();
            Map<String, String> requestBody = request.getBody();
            String account = requestBody.get("account");
            String password = requestBody.get("password");
            String email = requestBody.get("email");
            registerHandler.register(account, password, email);
            return ViewResource.of("/index.html", HttpStatus.OK);
        }
        if (request.getHttpMethod() == HttpMethod.GET) {
            return ViewResource.of("/register.html", HttpStatus.OK);
        }
        throw new IllegalArgumentException("잘못된 HTTP METHOD 요청입니다.");
    }
}
