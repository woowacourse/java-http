package org.apache.coyote.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.exception.UserNotFoundException;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        String requestUri = httpRequest.getRequestUri();
        Map<String, String> queryParams = httpRequest.getQueryParams();
        Map<String, String> headers = httpRequest.getHttpHeaders();

        String account = queryParams.get("account");
        String password = queryParams.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UserNotFoundException::new);
        if (user.checkPassword(password)) {
            log.info("user : {}", user);
        }
        String filePath = getClass().getClassLoader().getResource("static/login.html").getFile();
        String responseBody = null;
        try {
            responseBody = new String(Files.readAllBytes(new File(filePath).toPath()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return new HttpResponse(headers.get("Accept"), responseBody);
    }
}
