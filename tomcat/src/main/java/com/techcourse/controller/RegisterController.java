package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.List;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.util.FileReader;
import org.apache.coyote.util.HttpResponseBuilder;

public final class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse httpResponse) {
        String fileName = "register.html";
        List<String> contentLines = FileReader.readAllLines(fileName);
        HttpResponseBuilder.buildStaticContent(httpResponse, fileName, contentLines);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse httpResponse) {
        String account = request.getParams("account");
        String password = request.getParams("password");
        String email = request.getParams("email");

        InMemoryUserRepository.save(new User(account, password, email));

        HttpResponseBuilder.buildRedirection(httpResponse, "/");
    }
}
