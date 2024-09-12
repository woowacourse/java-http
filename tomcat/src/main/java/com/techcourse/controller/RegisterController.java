package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.coyote.http11.ViewResolver;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.StatusCode;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatus(StatusCode.OK);
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.setBody(ViewResolver.getInstance().resolveViewName("/register.html"));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        String requestBody = request.getBody();

        StringTokenizer tokenizer = new StringTokenizer(requestBody, "=|&");
        String account = "";
        String password = "";
        String email = "";
        while (tokenizer.hasMoreTokens()) {
            String key = tokenizer.nextToken();
            if (key.equals("account") && tokenizer.hasMoreTokens()) {
                account = tokenizer.nextToken();
            } else if (key.equals("password") && tokenizer.hasMoreTokens()) {
                password = tokenizer.nextToken();
            } else if (key.equals("email") && tokenizer.hasMoreTokens()) {
                email = tokenizer.nextToken();
            }
        }
        System.out.println("account = " + account);
        System.out.println("password = " + password);
        System.out.println("email = " + email);
        if (account.isBlank() || password.isBlank() || email.isBlank()) { // TODO: 예외처리 개선
            throw new IllegalArgumentException("올바르지 않은 request body 형식입니다.");
        }
        InMemoryUserRepository.save(new User(account, password, email));
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.sendRedirect("http://localhost:8080/index.html");
    }
}
