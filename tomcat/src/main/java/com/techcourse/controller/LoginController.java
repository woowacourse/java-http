package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.util.StaticResourceManager;
import java.util.Optional;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String STATIC_RESOURCE_PATH = "static/login.html";

    public HttpResponse login(HttpRequest request) {
        log.info("Query Parameters: {}", request.getQueryParameters());
        MediaType mediaType = MediaType.fromAcceptHeader(request.getAccept());

        Optional<String> userAccount = request.getQueryParameter("account");
        Optional<String> userPassword = request.getQueryParameter("password");

        if (userAccount.isEmpty() || userPassword.isEmpty()) {
            return new HttpResponse(200, "OK")
                    .addHeader("Content-Type", mediaType.getValue())
                    .setBody(StaticResourceManager.read(STATIC_RESOURCE_PATH));
        }

        return userAccount.map(InMemoryUserRepository::findByAccount)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(user -> user.checkPassword(userPassword.get()))
                .map(user -> HttpResponse.redirect("index.html"))
                .orElse(HttpResponse.redirect("401.html"));
    }
}
