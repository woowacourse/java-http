package com.techcourse.controller;

import com.spring.controller.AbstractController;
import com.spring.http.enums.HttpStatus;
import com.spring.http.request.HttpRequest;
import com.spring.http.response.HttpResponse;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.HttpStatusException;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import org.apache.catalina.util.FileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        final String fileName = request.requestStartLine().path() + ".html";
        final byte[] registerHtml = FileParser.loadStaticResourceByFileName(fileName);
        response.setBody(registerHtml);
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws IOException {
        final Map<String, String> formData = request.parseBody();

        final String account = formData.get("account");
        final String password = formData.get("password");
        final String email = formData.get("email");

        if (InMemoryUserRepository.existsByAccount(account)) {
            throw new HttpStatusException("이미 존재하는 사용자입니다.", HttpStatus.CONFLICT);
        }

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.setStatus(HttpStatus.FOUND);
        response.addHeader("Location", "/index.html");
        log.info("회원가입 완료 아이디 : {}", account);
    }
}
