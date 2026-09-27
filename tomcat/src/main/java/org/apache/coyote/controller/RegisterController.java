package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        URL resourceUrl = getClass().getClassLoader().getResource("static/register.html");

        if (resourceUrl == null) {
            throw new IllegalArgumentException("register.html 리소스를 찾을 수 없습니다.");
        }

        String registerPage = Files.readString(Paths.get(resourceUrl.toURI()), StandardCharsets.UTF_8);

        response.setBody(registerPage);
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.addHeader("Content-Length",
                String.valueOf(registerPage.getBytes(StandardCharsets.UTF_8).length));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> registerInfo = parseQueryString(request.getBody());
        String account = registerInfo.get("account");
        String password = registerInfo.get("password");
        String email = registerInfo.get("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.setStatus(302, "Found");
        response.setBody("");
        response.addHeader("Location", "/index.html");
        response.addHeader("Content-Length", "0");
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> parameters = new HashMap<>();

        for (String parameter : queryString.split("&")) {
            String[] keyValue = parameter.split("=", 2);

            if (keyValue.length != 2) {
                continue;
            }

            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);

            parameters.put(key, value);
        }

        return parameters;
    }
}
