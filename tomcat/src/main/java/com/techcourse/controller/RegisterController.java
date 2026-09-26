package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private static final String PATH_INDEX_HTML = "/index.html";
    private static final String PATH_REGISTER_HTML = "/register.html";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> parameters = parseFormData(request.getBody());
        User user = new User(parameters.get("account"), parameters.get("password"), parameters.get("email"));
        InMemoryUserRepository.save(user);
        log.info(user.toString());
        response.sendRedirect(PATH_INDEX_HTML);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.sendStaticHtml(PATH_REGISTER_HTML);
    }

    private Map<String, String> parseFormData(final String body) {
        Map<String, String> formData = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyAndMap = pair.split("=", 2);
            String key = keyAndMap[0];
            String value = keyAndMap[1];
            formData.put(URLDecoder.decode(key, StandardCharsets.UTF_8), URLDecoder.decode(value, StandardCharsets.UTF_8));
        }
        return formData;
    }
}
