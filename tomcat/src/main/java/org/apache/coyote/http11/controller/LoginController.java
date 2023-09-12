package org.apache.coyote.http11.controller;

import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.ViewLoader;
import org.apache.coyote.http11.request.RequestBody;

public class LoginController extends AbstractController {

    private static final int LOGIN_PARAMETER_SIZE = 2;

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final RequestBody requestBody = request.initRequestBody();
        if (Objects.isNull(requestBody) || requestBody.size() != LOGIN_PARAMETER_SIZE) {
            redirectByUnauthorized(response);
            return;
        }
        final String account = requestBody.get("account");
        final String password = requestBody.get("password");

        if (isValidUser(account, password)) {
            final Session session = request.getSession(true);
            session.setAttribute("user", getUser(account));
            redirectByFound(response, session);
        }
        redirectByUnauthorized(response);
    }

    private void redirectByUnauthorized(final HttpResponse response) {
        response
                .statusCode(StatusCode.UNAUTHORIZED)
                .contentType(ContentType.TEXT_HTML)
                .responseBody(ViewLoader.toUnauthorized());
    }

    private boolean isValidUser(final String account, final String password) {
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            return true;
        }
        return false;
    }

    private User getUser(final String account) {
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));
        return user;
    }

    private void redirectByFound(final HttpResponse response, final Session session) {
        response
            .statusCode(StatusCode.FOUND)
            .contentType(ContentType.TEXT_HTML)
            .responseBody(ViewLoader.toIndex())
            .addCookie(session.getId())
            .redirect("/index.html");
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.hasJSessionId() && Objects.nonNull(request.getSession(false))) {
            final Session session = request.getSession(false);
            final User user = (User) session.getAttribute("user");
            if (Objects.nonNull(user)) {
                response
                    .statusCode(StatusCode.FOUND)
                    .contentType(ContentType.TEXT_HTML)
                    .responseBody(ViewLoader.toIndex())
                    .redirect("/index.html");
                return;
            }
        }
        response
            .statusCode(StatusCode.OK)
            .contentType(ContentType.TEXT_HTML)
            .responseBody(ViewLoader.from("/login.html"));
    }
}
