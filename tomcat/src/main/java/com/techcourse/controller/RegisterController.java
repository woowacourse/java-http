package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.setResourceName("/register.html");
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        String body = request.getBody();
        Map<String, String> values = parseValues(body);
        String account = values.get("account");
        String password = values.get("password");
        String email = values.get("email");
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.found();
        response.redirectPage("/index.html");
    }

    private Map<String, String> parseValues(String body) {
        String tokens[] = body.split("&");
        if (tokens.length != 3) {
            throw new IllegalArgumentException("회원 가입 정보를 전부 입력해주세요.");
        }
        Map<String, String> values = new HashMap<>();
        for (String token : tokens) {
            int separatorIndex = token.indexOf('=');
            if (separatorIndex == -1) {
                throw new IllegalArgumentException("데이터 형식이 잘못되었습니다.");
            }
            values.put(token.substring(0, separatorIndex), token.substring(separatorIndex + 1));
        }
        return values;
    }
}
