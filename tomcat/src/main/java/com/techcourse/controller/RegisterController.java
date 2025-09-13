package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.catalina.ResourceResolver;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws URISyntaxException, IOException {
        ResourceResolver resourceResolver = new ResourceResolver();
        URL resource = resourceResolver.resolver(request.getRequestLine().getUrl());
        final String responseBody = Files.readString(Paths.get(resource.toURI()));
        return HttpResponse.ok(responseBody, TEXT_HTML_CHARSET_UTF_8);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        final String account = request.getBody().get("account");
        final String password = request.getBody().get("password");
        final String email = request.getBody().get("email");

        final User user = createUser(account, password, email);
        InMemoryUserRepository.save(user);
        return HttpResponse.redirection("index.html", TEXT_HTML_CHARSET_UTF_8);
    }

    private User createUser(String account, String password, String email) {
        Long id = 1L;
        return new User(++id, account, password, email);
    }
}
