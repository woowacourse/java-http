package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import org.was.view.View;
import org.was.view.ViewResolver;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.was.Controller.AbstractController;
import org.was.Controller.ResponseResult;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected ResponseResult doPost(HttpRequest request) {
        return null;
    }

    @Override
    protected ResponseResult doGet(HttpRequest request) {
        return null;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        try {
            if (!request.hasBodyData()) {
                throw new IllegalArgumentException("RequestBody is missing in the request");
            }

            Map<String, String> requestFormData = request.getFormData();
            String account = requestFormData.get("account");
            String password = requestFormData.get("password");
            String email = requestFormData.get("email");

            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            responseRegisterSuccess(response);

        } catch (IllegalArgumentException e) {
            response.setStatus400();
            response.setResponseBody(e.getMessage());
            log.info("Bad Request: {}", e.getMessage());

        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        responseRegisterPage(request, response);
    }

    private void responseRegisterSuccess(HttpResponse response) {
        response.setStatus302();
        response.setLocation("/index.html");
    }

    private void responseRegisterPage(HttpRequest request, HttpResponse response) throws IOException {
        View view = ViewResolver.getView("/register.html");
        response.setStatus200();
        response.setResponseBody(view.getContent());
        response.setContentType(request.getContentType());
    }
}
