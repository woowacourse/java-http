package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.StaticResource;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private static final String REGISTER_PAGE = "/register.html";
    private static final String INDEX_PAGE = "/index.html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        StaticResource.serve(response, REGISTER_PAGE);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> formData = request.getFormData();
        User user = new User(
            formData.get("account"),
            formData.get("password"),
            formData.get("email"));

        InMemoryUserRepository.save(user);
        log.info("register user: {}", user);

        response.sendRedirect(INDEX_PAGE);
    }
}
