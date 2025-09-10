package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        handleRegister(request, response);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.ok("/register.html");
    }

    private void handleRegister(HttpRequest request, HttpResponse response) {
        Map<String, String> requestBody = request.getRequestBody();
        User user = new User(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
        InMemoryUserRepository.save(user);
        response.found("/index.html");
    }
}
