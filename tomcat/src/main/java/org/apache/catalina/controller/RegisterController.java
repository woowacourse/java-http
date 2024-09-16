package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.http.HeaderName;
import org.apache.catalina.http.StatusCode;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getBodyParam("account");
        String password = request.getBodyParam("password");
        String email = request.getBodyParam("email");
        InMemoryUserRepository.save(new User(account, password, email));

        response.setStatusCode(StatusCode.FOUND);
        response.addHeader(HeaderName.LOCATION, "/index.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatusCode(StatusCode.OK);
        response.setBody("/register.html");
    }
}
