package org.apache.coyote.http11.controller;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        User user = new User(request.getBodyValue("account"), request.getBodyValue("password"),
                request.getBodyValue("email"));
        log.info("user: {}", user);
        InMemoryUserRepository.save(user);
        response.setStatus("302 FOUND");
        response.addHeader("Location", "/index.html");
    }
}
