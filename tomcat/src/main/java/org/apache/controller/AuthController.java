package org.apache.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;
import org.apache.util.PathUtils;

public class AuthController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        final Path path = PathUtils.load("/login.html");
        final String responseBody = new String(Files.readAllBytes(path));

        return new HttpResponse(HttpStatus.OK, ContentType.HTML, responseBody);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws Exception {
        final Path path = PathUtils.load("/login.html");
        final String responseBody = new String(Files.readAllBytes(path));
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();

        if (user.checkPassword(password)) {
            return new HttpResponse(HttpStatus.FOUND, ContentType.HTML, responseBody, "/index.html");
        }
        return new HttpResponse(HttpStatus.OK, ContentType.HTML, responseBody, "/401.html");
    }
}
