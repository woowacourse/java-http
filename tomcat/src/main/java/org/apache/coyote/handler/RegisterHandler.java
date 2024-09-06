package org.apache.coyote.handler;

import java.io.IOException;

import org.apache.coyote.HttpMethod;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponseGenerator;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterHandler {

    private static final RegisterHandler INSTANCE = new RegisterHandler();

    private RegisterHandler() {
    }

    public static RegisterHandler getInstance() {
        return INSTANCE;
    }

    public String processRegisterRequest(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.isSameMethod(HttpMethod.GET)) {
            return ResourceHandler.getInstance().handleSimpleResource("register.html");
        }

        if (httpRequest.isSameMethod(HttpMethod.POST)) {
            return processRegisterPostRequest(httpRequest);
        }

        return ResourceHandler.getInstance().handleSimpleResource("401.html");
    }

    private String processRegisterPostRequest(final HttpRequest httpRequest) {
        String[] body = httpRequest.getBody().split("&");
        String account = body[0].split("=")[1];
        String email = body[1].split("=")[1];
        String password = body[2].split("=")[1];
        InMemoryUserRepository.save(new User(account, password, email));
        return HttpResponseGenerator.getFoundResponse("http://localhost:8080/index.html");
    }

}
