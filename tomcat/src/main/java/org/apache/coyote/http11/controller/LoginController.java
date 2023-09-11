package org.apache.coyote.http11.controller;

import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.ViewLoader;
import org.apache.coyote.http11.request.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final int LOGIN_PARAMETER_SIZE = 2;

    @Override
    public HttpResponse handle(final HttpRequest request) {
        if (request.isGetRequest()) {
            return handleGetMethod(request);
        }
        return handlePostMethod(request);
    }

    private HttpResponse handleGetMethod(final HttpRequest request) {
        if (request.hasJSessionId() && Objects.nonNull(request.getSession(false))) {
            final Session session = request.getSession(false);
            final User user = (User) session.getAttribute("user");
            if (Objects.nonNull(user)) {
                return HttpResponse.builder()
                        .statusCode(StatusCode.FOUND)
                        .contentType(ContentType.TEXT_HTML)
                        .responseBody(ViewLoader.toIndex())
                        .redirect("/index.html")
                        .build();
            }
        }
        return HttpResponse.builder()
                .statusCode(StatusCode.OK)
                .contentType(ContentType.TEXT_HTML)
                .responseBody(ViewLoader.from("/login.html"))
                .build();
    }

    private HttpResponse handlePostMethod(final HttpRequest request) {
        final RequestBody requestBody = request.getRequestBody();
        if (Objects.isNull(requestBody) || requestBody.size() != LOGIN_PARAMETER_SIZE) {
            return HttpResponse.toUnauthorized();
        }
        final String account = requestBody.get("account");
        final String password = requestBody.get("password");
        final User user = login(account, password);

        final Session session = request.getSession(true);
        session.setAttribute("user", user);

        return HttpResponse.builder()
                .statusCode(StatusCode.FOUND)
                .contentType(ContentType.TEXT_HTML)
                .responseBody(ViewLoader.toIndex())
                .addCookie(session.getId())
                .redirect("/index.html")
                .build();
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
}
