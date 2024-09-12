package com.techcourse.controller;

import com.techcourse.exception.TechcourseException;
import com.techcourse.model.UserService;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.HttpRequestBody;
import org.apache.coyote.http.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final HttpRequestBody body = request.getBody();
        final UserService userService = UserService.getInstance();
        try {
            userService.register(body.get("account"), body.get("password"), body.get("email"));
            postRegisterSuccess(response);
        } catch (TechcourseException e) {
            response.setStatusCode(HttpStatusCode.BAD_REQUEST);
        }
    }

    private void postRegisterSuccess(final HttpResponse response) {
        final String location = "/index.html";
        response.setStatusCode(HttpStatusCode.FOUND);
        response.setLocation(location);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String path = "/register.html";
        final String body = viewResolver.resolve(path);
        response.setStatusCode(HttpStatusCode.OK);
        response.setContent(path, body);
    }
}
