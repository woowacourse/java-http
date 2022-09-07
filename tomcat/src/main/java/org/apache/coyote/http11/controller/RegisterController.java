package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.ControllerAdvice;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends Controller {

    private final UserService userService;

    public RegisterController() {
        this.userService = new UserService();
    }

    @Override
    public HttpResponse getResponse(HttpRequest httpRequest) throws IOException {
        if (httpRequest.getHttpMethod() == HttpMethod.GET) {
            return HttpResponse.createWithBody(HttpStatus.OK, httpRequest.getRequestLine());
        }
        if (httpRequest.getHttpMethod() == HttpMethod.POST) {
            String account = httpRequest.getBodyValue("account");
            String email = httpRequest.getBodyValue("email");
            String password = httpRequest.getBodyValue("password");
            userService.register(account, email, password);
            return HttpResponse.createWithoutBody(HttpStatus.FOUND, "/index");
        }
        return ControllerAdvice.handleNotFound();
    }
}
