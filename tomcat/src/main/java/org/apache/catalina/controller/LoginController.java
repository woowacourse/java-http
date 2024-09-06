package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.view.View;
import org.apache.catalina.view.ViewResolver;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        responseLoginPage(response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String requestBody = request.getRequestBody();
        if (requestBody == null) {
            throw new IllegalArgumentException("Query string is missing in the request");
        }

        Map<String, String> requestForm = extractFormData(requestBody);
        String userName = requestForm.get("account");
        String password = requestForm.get("password");

        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(userName);
        if (optionalUser.isPresent() && optionalUser.get().checkPassword(password)) {
            responseLoginSuccess(response);
        } else {
            responseLoginFail(response);
        }
    }

    private void responseLoginPage(HttpResponse response) throws IOException {
        View view = ViewResolver.getView("/login.html");
        response.setStatus200();
        response.setResponseBody(view.getContent());
        response.setContentTypeHtml();
    }

    private void responseLoginSuccess(HttpResponse response) {
        response.setStatus302();
        response.setLocation("/index.html");
    }

    private void responseLoginFail(HttpResponse response) {
        response.setStatus302();
        response.setLocation("/401.html");
    }

    private Map<String, String> extractFormData(String requestBody) {
        Map<String, String> requestData = new HashMap<>();
        String[] keyValuePairs = requestBody.split("&");
        for (String keyValuePair : keyValuePairs) {
            String[] keyValue = keyValuePair.split("=");
            requestData.put(keyValue[0], keyValue[1]);
        }

        return requestData;
    }
}
