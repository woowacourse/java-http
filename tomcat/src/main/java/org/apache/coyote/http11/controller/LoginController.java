package org.apache.coyote.http11.controller;

import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.ViewLoader;
import org.apache.coyote.http11.request.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final int LOGIN_PARAMETER_SIZE = 2;

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final RequestBody requestBody = request.initRequestBody();
        if (Objects.isNull(requestBody) || requestBody.size() != LOGIN_PARAMETER_SIZE) {
            response
                .statusCode(StatusCode.UNAUTHORIZED)
                .contentType(ContentType.TEXT_HTML)
                .responseBody(ViewLoader.toUnauthorized());
            return;
        }
        final String account = requestBody.get("account");
        final String password = requestBody.get("password");
        final User user = login(account, password);

        final Session session = request.getSession(true);
        session.setAttribute("user", user);

        response
            .statusCode(StatusCode.FOUND)
            .contentType(ContentType.TEXT_HTML)
            .responseBody(ViewLoader.toIndex())
            .addCookie(session.getId())
            .redirect("/index.html");
    }

    private User login(final String account, final String password) {
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));
        validatePassword(user, password);
        return user;
    }

    private void validatePassword(final User user, final String password) {
        if (user.checkPassword(password)) {
            return;
        }
        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
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
