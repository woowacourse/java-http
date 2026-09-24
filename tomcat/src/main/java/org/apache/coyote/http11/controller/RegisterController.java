package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {
    private static final String INDEX_PAGE = "/index.html";

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    public RegisterController() {
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        if (request.requestHeader().getContentLength().isEmpty()) {
            return HttpResponse.status(HttpStatus.LENGTH_REQUIRED);
        }

        registerUser(request.requestBody());

        return HttpResponse.found().location(INDEX_PAGE);
    }

    private void registerUser(HttpRequestBody body) {
        String[] formData = body.requestBody().split("&");

        List<String> data = Arrays.asList(formData);

        String account = data.get(0).split("=")[1];
        String email = data.get(1).split("=")[1];
        String password = data.get(2).split("=")[1];

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        log.info("new user : {}", user);
    }
}
