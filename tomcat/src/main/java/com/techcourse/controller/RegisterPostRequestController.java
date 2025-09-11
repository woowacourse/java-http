package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.RequestMethod;

public class RegisterPostRequestController implements Controller {

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.getRequestMethod() == RequestMethod.POST &&
                httpRequest.getRequestUrl()
                        .equals("/register");
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        String account = httpRequest.getParameter("account");
        String password = httpRequest.getParameter("password");
        String email = httpRequest.getParameter("email");
        InMemoryUserRepository.save(new User(account, password, email));

        httpResponse.redirect("http://localhost:8080/index.html");
    }
}
