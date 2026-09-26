package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String INDEX_PAGE = "/index.html";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> parameters = request.getParameters();
        String account = parameters.get(ACCOUNT);
        String password = parameters.get(PASSWORD);
        String email = parameters.get(EMAIL);

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.sendRedirect(INDEX_PAGE);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.forward(request.getPath());
    }
}
