package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final RegisterController INSTANCE = new RegisterController();

    public static RegisterController getInstance() {
        return INSTANCE;
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        return new HttpResponse(HttpStatusCode.OK, ContentType.HTML, "/register.html");
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        User user = createNewUser(request);
        InMemoryUserRepository.save(user);
        log.info("회원가입 성공! 아이디 : {}", user.getAccount());
        HttpResponse response = new HttpResponse(HttpStatusCode.FOUND, ContentType.HTML, "/index.html");
        response.setLocation("/index.html");
        return response;
    }

    private User createNewUser(HttpRequest request) {
        Map<String, String> requestBody = request.parseQueryStringForm(request.getBody());
        Long id = InMemoryUserRepository.getNextId();
        String account = requestBody.get("account");
        String password = requestBody.get("password");
        String email = requestBody.get("email");
        return new User(id, account, password, email);
    }
}
