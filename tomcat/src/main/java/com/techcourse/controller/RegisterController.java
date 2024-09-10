package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.util.StaticResourceManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.common.HttpStatusCode;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.MediaType;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final String STATIC_RESOURCE_PATH = "static/register.html";

    public void doGet(HttpRequest request, HttpResponse response) {
        MediaType mediaType = MediaType.fromAcceptHeader(request.getHeaders().get("Accept"));
        response.setStatus(HttpStatusCode.OK)
                .addHeader("Content-Type", mediaType.getValue())
                .setBody(StaticResourceManager.read(STATIC_RESOURCE_PATH));
    }

    public void doPost(HttpRequest httpRequest, HttpResponse response) {
        Map<String, String> body = Arrays.stream(httpRequest.getBody().split("&"))
                .map(s -> s.split("="))
                .collect(HashMap::new, (m, e) -> m.put(e[0], e[1]), Map::putAll);

        String account = body.get("account");
        String password = body.get("password");
        String email = body.get("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.sendRedirect("index.html");
    }
}
