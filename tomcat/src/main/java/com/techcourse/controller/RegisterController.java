package com.techcourse.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String RESOURCE_BASE_PATH = "static";

    @Override
    protected void doGet(HttpRequest request, HttpResponse.HttpResponseBuilder response) throws Exception {
        String resource = ensureHtmlExtension(request.getPath());
        String responseBody = loadResourceContent(resource);
        buildOkResponse(responseBody, response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse.HttpResponseBuilder response) {
        String requestBody = request.getRequestBody();
        String account = requestBody.split("&")[0].split("=")[1];
        String email = requestBody.split("&")[1].split("=")[1];
        String password = requestBody.split("&")[2].split("=")[1];
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        buildRedirectResponse("/index.html", response);
    }

    private String ensureHtmlExtension(String path) {
        if (!path.contains(".")) {
            path += ".html";
        }

        return path;
    }

    private String loadResourceContent(String resource) throws IOException {
        String resourcePath = Objects.requireNonNull(getClass().getClassLoader()
                        .getResource(RESOURCE_BASE_PATH + resource))
                .getPath();

        try (FileInputStream file = new FileInputStream(resourcePath)) {
            return new String(file.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void buildOkResponse(String responseBody, HttpResponse.HttpResponseBuilder response) {
        response.withStatusCode("200 OK")
                .withResponseBody(responseBody)
                .addHeader("Content-Type", "text/html")
                .addHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
    }

    private void buildRedirectResponse(String location, HttpResponse.HttpResponseBuilder response) {
        response.withStatusCode("302 Found")
                .addHeader("Location", location);
    }
}
