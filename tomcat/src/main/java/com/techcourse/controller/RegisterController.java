package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.util.StaticResourceManager;
import java.util.Map;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController {
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final String STATIC_RESOURCE_PATH = "static/register.html";

    public HttpResponse doGet(HttpRequest request) {
        MediaType mediaType = MediaType.fromAcceptHeader(request.getAccept());
        return new HttpResponse(200, "OK")
                .addHeader("Content-Type", mediaType.getValue())
                .setBody(StaticResourceManager.read(STATIC_RESOURCE_PATH));
    }

    public HttpResponse doPost(HttpRequest httpRequest) {
        Map<String, String> body = httpRequest.parseFormBody();

        String account = body.get("account");
        String password = body.get("password");
        String email = body.get("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return HttpResponse.redirect("index.html");
    }
}
