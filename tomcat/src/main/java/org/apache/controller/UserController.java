package org.apache.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;
import org.apache.util.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws Exception {
        final String url = request.getUrl();
        final Path path = PathUtils.load(url + ".html");
        saveUser(request);
        final String responseBody = new String(Files.readAllBytes(path));
        return new HttpResponse(HttpStatus.FOUND, ContentType.HTML, responseBody, "/index.html");
    }

    private void saveUser(final HttpRequest request) {
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");
        final String email = request.getParameter("email");

        final Optional<User> findUser = InMemoryUserRepository.findByAccount(account);
        if (findUser.isPresent()) {
            log.info("존재하는 유저입니다.");
            return;
        }
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("회웝가입 성공 user={}", user);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        final String url = request.getUrl();
        final Path path = PathUtils.load(url + ".html");
        final String responseBody = new String(Files.readAllBytes(path));
        return new HttpResponse(HttpStatus.OK, ContentType.HTML, responseBody);
    }
}
