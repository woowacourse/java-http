package com.techcourse.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;

public class RegisterController extends AbstractController {

    private static final String RESOURCE_BASE_PATH = "static";

    @Override
    protected void doGet(HttpRequest request, HttpResponse.HttpResponseBuilder response) throws Exception {
        String resource = ensureHtmlExtension(request.getPath());
        String responseBody = loadResourceContent(resource);
        String contentType = response.getContentType(resource);
        buildOkResponse(responseBody, contentType, response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse.HttpResponseBuilder response) {
        String account = request.getParameter("account");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

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

    private void buildOkResponse(String responseBody, String contentType, HttpResponse.HttpResponseBuilder response) {
        response.withStatusCode(StatusCode.OK)
                .withResponseBody(responseBody)
                .addHeader("Content-Type", contentType)
                .addHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
    }

    private void buildRedirectResponse(String location, HttpResponse.HttpResponseBuilder response) {
        response.withStatusCode(StatusCode.FOUND)
                .addHeader("Location", location);
    }
}
