package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpResponseStatus;

public class SignupController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        request.setPath("/register.html");
        serveStaticFile(request.getPath(), response, "text/html;charset=utf-8");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getFormParam("account");
        String password = request.getFormParam("password");
        String email = request.getFormParam("email");

        if (account == null || password == null) {
            response.sendRedirect(HttpResponseStatus.FOUND, "/401.html");
            return;
        }

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        response.sendRedirect(HttpResponseStatus.FOUND, "/index.html");
    }

    private void serveStaticFile(String path, HttpResponse response, String contentType) throws IOException, URISyntaxException {
        final var resource = getClass().getClassLoader().getResource("static" + path);
        if (resource != null) {
            final Path resourcePath = Paths.get(resource.toURI());
            byte[] body = Files.readAllBytes(resourcePath);
            response.addHeader("Content-Type", contentType);
            response.setBody(body);
        }
        response.send();
    }
}
