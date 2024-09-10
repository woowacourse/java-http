package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.session.SessionManager;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;
import org.apache.catalina.servlet.RequestMapping;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.startline.HttpStatus;

@RequestMapping("/login")
public class LoginController extends RestController {

    @Override
    boolean doGet(HttpRequest request, HttpResponse response) {
        return false;
    }

    @Override
    boolean doPost(HttpRequest request, HttpResponse response) {
        Optional<User> nullableUser = InMemoryUserRepository.findByAccount(request.getFromBody("account"));
        return nullableUser
                .filter(user -> user.checkPassword(request.getFromBody("password")))
                .map(user -> loginAndRedirectToIndex(response, user))
                .orElseGet(() -> responsePage401(response));
    }

    private boolean loginAndRedirectToIndex(HttpResponse response, User user) {
        String jSessionId = SessionManager.addUser(user);
        response.addSessionToCookies(jSessionId);
        redirectTo(response, "/index");
        return response.isValid();
    }

    private boolean responsePage401(HttpResponse response) {
        String path = "static/401.html";
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("Could not find resource " + path);
        }
        response.setStatus(HttpStatus.UNAUTHORIZED);

        try {
            return responseResource(response, Path.of(resource.toURI()));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("cannot convert to URI: " + resource);
        }
    }
}
