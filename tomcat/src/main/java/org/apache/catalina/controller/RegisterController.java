package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import org.apache.catalina.view.View;
import org.apache.catalina.view.ViewResolver;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
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
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        responseRegisterPage(response);
    }

    private void responseRegisterSuccess(HttpResponse response) {
        response.setStatus302();
        response.setLocation("/index.html");
    }

    private void responseRegisterPage(HttpResponse response) throws IOException {
        View view = ViewResolver.getView("/register.html");
        response.setStatus200();
        response.setResponseBody(view.getContent());
        response.setContentTypeHtml();
    }
}
