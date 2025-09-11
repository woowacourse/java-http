package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import java.util.Map;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.constant.ContentType;
import org.apache.coyote.http11.constant.HttpStatus;
import org.apache.coyote.util.FileReader;

public class UserRegisterController extends AbstractController {

    public UserRegisterController() {
        super("/register");
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setStatusCode(HttpStatus.OK);
        httpResponse.setContentType(ContentType.HTML);
        httpResponse.setBody(FileReader.readFile("register.html"));
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        final Map<String, String> requestBody = httpRequest.parseBody();
        InMemoryUserRepository.save(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
        httpResponse.setStatusCode(HttpStatus.FOUND);
        httpResponse.setContentType(ContentType.HTML);
        httpResponse.appendHeader("Location", "http://localhost:8080/index.html");
    }

}
