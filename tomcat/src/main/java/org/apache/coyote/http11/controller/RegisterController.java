package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.apache.coyote.http11.model.request.FormParameters;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.Http11Response;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, Http11Response response) throws Exception {
        URL resource = RegisterController.class.getClassLoader().getResource("static/register.html");
        Path path = Path.of(resource.toURI());
        response.setBody(Files.readAllBytes(path));
        response.ok("text/html");
    }

    @Override
    protected void doPost(HttpRequest request, Http11Response response) {
        try {
            Map<String, String> values = FormParameters.from(request.getRequestBody().value()).values();
            String account = required(values, "account");
            String password = required(values, "password");
            String email = required(values, "email");

            InMemoryUserRepository.save(new User(account, password, email));
            response.redirect("/index.html");
        } catch (IllegalArgumentException e) {
            response.redirect("/401.html");
        }
    }

    private String required(Map<String, String> values, String name) {
        String value = values.get(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("필수 회원 정보가 없습니다: " + name);
        }
        return value;
    }
}
