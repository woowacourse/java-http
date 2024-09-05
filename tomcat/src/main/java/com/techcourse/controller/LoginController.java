package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.util.FileReader;
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
        String pathStr = request.getPath();
        String requestedExtension = pathStr.substring(pathStr.lastIndexOf(".") + 1);
        MediaType mediaType = MediaType.ofExtension(requestedExtension);
        log.info("Requested MediaType: {}", mediaType);
        log.info("Query Parameters: {}", request.getQueryParameters());

        Optional<User> user = InMemoryUserRepository.findByAccount(request.getQueryParameter("account"));
        user.ifPresentOrElse(this::logUser, () -> log.info("User not found"));

        String fileContent = FileReader.read(STATIC_RESOURCE_PATH);
        return new HttpResponse(1.1, 200, "OK")
                .addHeader("Content-Type", mediaType.getValue())
                .addHeader("Content-Length", String.valueOf(fileContent.getBytes().length))
                .setBody(fileContent);
    }

    private void logUser(User user) {
        if (user.checkPassword("password")) {
            log.info("User: {}", user);
            return;
        }
        log.info("Login Fail");
    }
}
