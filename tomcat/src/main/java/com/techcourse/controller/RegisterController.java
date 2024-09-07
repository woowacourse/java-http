package com.techcourse.controller;

import com.techcourse.service.RegisterService;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.view.ViewResolver;

public class RegisterController implements Controller{

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.isSameMethod(HttpMethod.GET)) {
            response.setView(ViewResolver.getView("register.html"));
            response.setStatus(HttpStatus.OK);
            response.setHeaders(HttpHeaders.of(request, response));
        }
        if (request.isSameMethod(HttpMethod.POST)) {
            RegisterService.addUser(request.findFromBody("account"),
                    request.findFromBody("password"),
                    request.findFromBody("email"));
            log.info("[REGISTER] {}", request.findFromBody("account"));
            response.setView(ViewResolver.getView("register.html"));
            response.setStatus(HttpStatus.FOUND);
            response.setHeaders(HttpHeaders.of(request, response));
            response.addHeader("Location", "/index.html");
        }
    }
}
