package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.FormData;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;

public class LoginController extends AbstractController {
    private static final String LOGIN_PAGE = "static/login.html";
    private static final String USER_SESSION_ATTRIBUTE = "user";
    private static final String INDEX_PATH = "/index.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";
    private static final String ACCOUNT_PARAMETER = "account";
    private static final String PASSWORD_PARAMETER = "password";

    private final SessionManager sessionManager;

    public LoginController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws URISyntaxException, IOException {

        if (sessionManager.isSessionContainsKey(request.getSessionId(), USER_SESSION_ATTRIBUTE)) {
            response.setStatus(HttpStatus.FOUND);
            response.setLocation(INDEX_PATH);
            return;
        }
        URL resource = getClass().getClassLoader().getResource(LOGIN_PAGE);
        response.setContentType(ContentType.HTML);
        response.setBody(Files.readString(Paths.get(resource.toURI()), StandardCharsets.UTF_8));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        FormData formData = FormData.from(request.getBody());

        String account = formData.get(ACCOUNT_PARAMETER);
        String password = formData.get(PASSWORD_PARAMETER);

        Optional<User> matchedUser = InMemoryUserRepository
                .findByAccount(account)
                .filter(user -> user.checkPassword(password));

        if (matchedUser.isPresent()) {
            Session session = sessionManager.getOrCreateSession(request.getSessionId());
            session.addUser(USER_SESSION_ATTRIBUTE, matchedUser.get());

            response.setStatus(HttpStatus.FOUND);
            response.setLocation(INDEX_PATH);
            return;
        }
        response.setStatus(HttpStatus.FOUND);
        response.setLocation(UNAUTHORIZED_PATH);
    }
}
