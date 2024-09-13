package com.techcourse.controller;

import java.io.IOException;
import java.util.Map;

import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterController extends AbstractController {

    private static final RegisterController INSTANCE = new RegisterController();

    private RegisterController() {}

    public static RegisterController getInstance() {
        return INSTANCE;
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> queryStrings = request.getBodyQueryStrings();

        User user = new User(queryStrings.get(ACCOUNT), queryStrings.get(PASSWORD), queryStrings.get(EMAIL));
        InMemoryUserRepository.save(user);
        redirectTo(response, INDEX_PAGE);
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        serveStaticFile(request, response);
    }
}
