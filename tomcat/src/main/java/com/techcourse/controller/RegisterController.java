package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private static final String PATH_INDEX_HTML = "/index.html";
    private static final String PATH_REGISTER_HTML = "/register.html";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> parameters = request.getFormData();
        User user = new User(parameters.get("account"), parameters.get("password"), parameters.get("email"));
        InMemoryUserRepository.save(user);
        log.info(user.toString());
        response.sendRedirect(PATH_INDEX_HTML);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.sendStaticHtml(PATH_REGISTER_HTML);
    }
}
