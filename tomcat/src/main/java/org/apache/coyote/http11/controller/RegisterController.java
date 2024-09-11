package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.util.Map;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterController extends AbstractController {

    private static final RegisterController instance = new RegisterController();

    private RegisterController() {}

    public static RegisterController getInstance() {
        return instance;
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> pairs = request.getBodyQueryString();

        InMemoryUserRepository.save(new User(pairs.get("account"), pairs.get("password"), pairs.get("email")));
        redirectTo(response, "/index.html");
    }

    private void redirectTo(HttpResponse response, String location) throws IOException {
        response.addStatusLine("HTTP/1.1 302 Found");
        response.addHeader("Location", "http://localhost:8080" + location);
        response.writeResponse();
    }
}
