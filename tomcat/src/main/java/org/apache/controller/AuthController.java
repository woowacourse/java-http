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
import org.apache.coyote.http.session.Session;
import org.apache.coyote.http.session.SessionManager;
import org.apache.util.PathUtils;

public class AuthController extends AbstractController {

    private static final String LOGIN_PAGE = "/login.html";
    public static final String INDEX_PAGE = "/index.html";

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        final Path path = PathUtils.load(LOGIN_PAGE);
        final String responseBody = new String(Files.readAllBytes(path));
        final Optional<User> loginUser = request.findUserByJSessionId();
        if (loginUser.isPresent()) {
            return redirectByAlreadyLogin(responseBody);
        }
        return new HttpResponse(HttpStatus.OK, ContentType.HTML, responseBody);
    }

    private HttpResponse redirectByAlreadyLogin(final String responseBody) {
        return new HttpResponse(HttpStatus.FOUND, ContentType.HTML, responseBody, INDEX_PAGE);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws Exception {
        final Path path = PathUtils.load(LOGIN_PAGE);
        final String responseBody = new String(Files.readAllBytes(path));
        return createLoginResponse(request, responseBody);
    }

    private HttpResponse createLoginResponse(final HttpRequest request, final String responseBody) {
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");

        if (account.isEmpty() || password.isEmpty() || !isSuccessLogin(account, password)) {
            return new HttpResponse(HttpStatus.FOUND, ContentType.HTML, responseBody, INDEX_PAGE);
        }
        final User user = new User(account, password);
        return successLoginResponse(user, responseBody);
    }

    private HttpResponse successLoginResponse(final User user, final String responseBody) {
        final HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND, ContentType.HTML, responseBody,
                INDEX_PAGE);
        final Session session = new Session();
        session.setAttribute("user", user);
        SessionManager.add(session);
        httpResponse.addJSessionId(session);
        return httpResponse;
    }

    private boolean isSuccessLogin(final String account, final String password) {
        final Optional<User> findUser = InMemoryUserRepository.findByAccount(account);
        return findUser.filter(user -> user.checkPassword(password))
                .isPresent();
    }
}
