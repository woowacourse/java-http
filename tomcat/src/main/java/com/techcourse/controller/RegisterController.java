package com.techcourse.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;

public class RegisterController extends AbstractController {

    private static final String RESOURCE_BASE_PATH = "static";
    private static final String ACCOUNT_PARAM = "account";
    private static final String EMAIL_PARAM = "email";
    private static final String PASSWORD_PARAM = "password";
    private static final String INDEX_PATH = "/index.html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String resource = ensureHtmlExtension(request.getPath());
        String responseBody = loadResourceContent(resource);
        String contentType = response.getContentType(resource);
        buildOkResponse(responseBody, contentType, response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter(ACCOUNT_PARAM);
        String email = request.getParameter(EMAIL_PARAM);
        String password = request.getParameter(PASSWORD_PARAM);

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        buildRedirectResponse(INDEX_PATH, response);
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

    private void buildOkResponse(String responseBody, String contentType, HttpResponse response) {
        response.setStatusCode(StatusCode.OK);
        response.setResponseBody(responseBody);
        response.addHeader(HttpHeader.CONTENT_TYPE.getValue(), contentType);
        response.addHeader(HttpHeader.CONTENT_LENGTH.getValue(), String.valueOf(responseBody.getBytes().length));
    }

    private void buildRedirectResponse(String location, HttpResponse response) {
        response.setStatusCode(StatusCode.FOUND);
        response.addHeader(HttpHeader.LOCATION.getValue(), location);
    }
}
