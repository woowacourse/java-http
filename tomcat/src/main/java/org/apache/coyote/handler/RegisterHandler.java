package org.apache.coyote.handler;

import org.apache.http.HttpMethod;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponseGenerator;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterHandler extends Handler {

    private static final RegisterHandler INSTANCE = new RegisterHandler();

    private RegisterHandler() {
    }

    public static RegisterHandler getInstance() {
        return INSTANCE;
    }

    public String handle(final HttpRequest httpRequest) {
        if (httpRequest.isSameMethod(HttpMethod.GET)) {
            return StaticResourceHandler.getInstance().handle(new HttpRequest("GET", "/register.html", "HTTP/1.1", null, null));
        }

        if (httpRequest.isSameMethod(HttpMethod.POST)) {
            return processRegisterPostRequest(httpRequest);
        }

        return StaticResourceHandler.getInstance().handle(new HttpRequest("GET", "/401.html", "HTTP/1.1", null, null));
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
