package com.techcourse.controller;


import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.util.RequestBodyParser;
import org.apache.coyote.view.StaticResourceView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        new StaticResourceView("register.html").render(response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> formData = RequestBodyParser.parseFormData(request.getBody());
        User user = new User(formData.get("account"), formData.get("email"), formData.get("password"));
        InMemoryUserRepository.save(user);
        log.info("{} - 회원 가입 성공", user);
        response.sendRedirect("/index.html");
    }
}
