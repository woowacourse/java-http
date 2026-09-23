package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.FormData;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class RegisterController extends AbstractController {

    private static final String REGISTER_PAGE = "static/register.html";
    private static final String INDEX_PATH = "/index.html";
    private static final String ACCOUNT_PARAMETER = "account";
    private static final String PASSWORD_PARAMETER = "password";
    private static final String EMAIL_PARAMETER = "email";
    private static final String DUPLICATE_USER_MESSAGE = "이미 존재하는 회원입니다.";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader().getResource(REGISTER_PAGE);
        response.setContentType(ContentType.HTML);
        response.setBody(Files.readString(Paths.get(resource.toURI()), StandardCharsets.UTF_8));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        FormData formData = FormData.from(request.getBody());
        User user = new User(
                formData.get(ACCOUNT_PARAMETER),
                formData.get(PASSWORD_PARAMETER),
                formData.get(EMAIL_PARAMETER)
        );
        if (!InMemoryUserRepository.saveIfAbsent(user)) {
            response.setStatus(HttpStatus.CONFLICT);
            response.setContentType(ContentType.TEXT);
            response.setBody(DUPLICATE_USER_MESSAGE);
            return;
        }
        response.setStatus(HttpStatus.FOUND);
        response.setLocation(INDEX_PATH);
    }
}
