package com.techcourse.controller;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String SUPPORT_CONTENT_TYPE = "application/x-www-form-urlencoded";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.sendStaticResource("/register.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        if (isNotSupport(request)) {
            throw new UncheckedServletException("지원하지 않는 요청입니다.");
        }
        doRegister(request, response);
    }

    private void doRegister(HttpRequest request, HttpResponse response) {
        Map<String, String> formData = parsedFormData(request.getBody());
        User user = createUser(formData);
        Session session = createUserSession(user);

        response.sendRedirect("/index.html");
        response.setSession(session);
    }

    private boolean isNotSupport(HttpRequest request) {
        return !SUPPORT_CONTENT_TYPE.equals(request.getContentType());
    }

    private Map<String, String> parsedFormData(String body) {
        Map<String, String> formData = new HashMap<>();
        for (String data : body.split("&")) {
            String[] dataComponent = data.split("=");
            formData.put(dataComponent[0], dataComponent[1]);
        }
        return formData;
    }

    private User createUser(Map<String, String> formData) {
        String account = formData.get("account");
        String password = formData.get("password");
        String email = formData.get("email");
        return new User(account, password, email);
    }

    private Session createUserSession(User user) {
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user);
        return session;
    }
}
