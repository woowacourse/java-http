package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.response.Status;

import java.util.Map;

public class RegisterController extends MappingController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.forward(request.getUri());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> requestBody = request.getBody();
        User user = new User(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
        InMemoryUserRepository.save(user);

        response.setStatusLine(Status.OK);
        response.sendRedirect("/index.html");
    }
}
